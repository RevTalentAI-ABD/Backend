package com.revtalent.revtalent.controller;

import com.revtalent.revtalent.dto.AIRequest;
import com.revtalent.revtalent.model.AIDocument;
import com.revtalent.revtalent.repository.AIDocumentRepository;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;

import java.nio.file.Files;

import java.util.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin("*")
public class AIChatController {

    @Autowired
    private AIDocumentRepository repository;

    @PostMapping("/ask")
    public Map<String, String> askAI(
            @RequestBody AIRequest request
    ) {

        try {

            String question =
                    request.getQuestion();

            List<AIDocument> docs =
                    repository.findByIncludedTrue();

            StringBuilder context =
                    new StringBuilder();

            // READ DOCUMENTS

            for (AIDocument doc : docs) {

                try {

                    File file =
                            new File(
                                    doc.getFilePath()
                            );

                    if (!file.exists()) {
                        continue;
                    }

                    String text = "";

                    // PDF

                    if (doc.getFileName()
                            .endsWith(".pdf")) {

                        PDDocument pdf =
                                PDDocument.load(file);

                        PDFTextStripper stripper =
                                new PDFTextStripper();

                        text =
                                stripper.getText(pdf);

                        pdf.close();
                    }

                    // DOCX

                    else if (doc.getFileName()
                            .endsWith(".docx")) {

                        XWPFDocument document =
                                new XWPFDocument(
                                        new FileInputStream(file)
                                );

                        XWPFWordExtractor extractor =
                                new XWPFWordExtractor(document);

                        text =
                                extractor.getText();

                        extractor.close();
                    }

                    // TXT

                    else {

                        text =
                                Files.readString(
                                        file.toPath()
                                );
                    }

                    // LIMIT CONTEXT SIZE

                    if (text.length() > 2000) {

                        text =
                                text.substring(
                                        0,
                                        2000
                                );
                    }

                    context.append(text)
                            .append("\n\n");

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            // FINAL PROMPT

            String finalPrompt =

                    """
                    You are an intelligent HR AI assistant.

                    Answer clearly and briefly.

                    Use ONLY the provided HR documents.

                    If answer is not found,
                    say:

                    "Information not found in uploaded documents."

                    HR Documents:
                    """
                            +

                            context +

                            """

                            User Question:
                            """
                            +

                            question;

            // OLLAMA REQUEST

            RestTemplate restTemplate =
                    new RestTemplate();

            Map<String, Object> body =
                    new HashMap<>();

            body.put(
                    "model",
                    "mistral:7b"
            );

            body.put(
                    "prompt",
                    finalPrompt
            );

            body.put(
                    "stream",
                    false
            );

            body.put(
                    "keep_alive",
                    "30m"
            );

            body.put(

                    "options",

                    Map.of(

                            "num_predict",
                            200,

                            "temperature",
                            0.2,

                            "top_k",
                            20,

                            "top_p",
                            0.8
                    )
            );

            HttpHeaders headers =
                    new HttpHeaders();

            headers.setContentType(
                    MediaType.APPLICATION_JSON
            );

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(
                            body,
                            headers
                    );

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(

                            "http://localhost:11434/api/generate",

                            entity,

                            Map.class
                    );

            String aiResponse =
                    response.getBody()
                            .get("response")
                            .toString();

            return Map.of(
                    "response",
                    aiResponse
            );

        } catch (Exception e) {

            e.printStackTrace();

            return Map.of(
                    "response",
                    "AI failed: " + e.getMessage()
            );
        }
    }
}