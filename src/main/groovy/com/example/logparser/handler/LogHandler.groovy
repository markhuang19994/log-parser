package com.example.logparser.handler

import com.example.logparser.model.LogDetail
import com.example.logparser.LogParser
import com.example.logparser.model.ProcessDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

import java.nio.charset.StandardCharsets
import java.util.stream.Collectors


@RestController()
@RequestMapping("/log")
class LogHandler {

    private String log = ""
    private String logStructure = ""

    @PostMapping("/init")
    String initLogs(@RequestParam(value = "logStructure") String logStructure,
            @RequestParam(value = "logFiles", required = false) List<MultipartFile> logFiles) {
        this.logStructure = logStructure
        StringBuilder sb = new StringBuilder()
        for (logFile in logFiles) {
            sb.append(new String(logFile.bytes, StandardCharsets.UTF_8))
        }
        log = sb.toString()
        "success"
    }


    @PostMapping("/category/processId")
    ResponseEntity<?> getCategoryByProcessIdLogDetail() {
        //"%time [%projectName] [%nodeName] [%commitHash] [%thread] [%processId] [%requestId] | %requestPath | %userId | %className [%logStatus] %content"
        LogParser parser = LogParser.getInstance(logStructure)
        List<String> logList = parser.readLog(log, null)
        List<LogDetail> logDetails = parser.parseLog(logList)


        Map<String, List<LogDetail>> m = logDetails.stream()
                .collect(Collectors.groupingBy({ ((LogDetail) it).getAttributeMap().get("processId") }))
        m.remove("")

        List<ProcessDetail> processDetails = new ArrayList<>()
        for (Map.Entry<String, List<LogDetail>> entry : m.entrySet()) {
            List<LogDetail> processLogDetails = new ArrayList<>()
            String processId = entry.getKey()
            String userId = ""
            for (LogDetail logDetail : entry.getValue()) {
                String content = logDetail.getAttributeMap().get("content")
                if (content != null && content.matches("^ ?Debug(Map|String|List)>>>[\\s\\S]*")) {
                    if (content.matches(".*?\\{\"ProcessInfo\":[\\s\\S]*")) {
                        userId = content.replaceAll(".*?\"userId\":\"(.*?)\"[\\s\\S]*", '$1')
                    }
                    processLogDetails.add(logDetail)
                }
            }
            processDetails.add(new ProcessDetail(processId, userId, processLogDetails))
        }

        processDetails = processDetails.stream().filter({ p -> "" != p.userId }).collect(Collectors.toList())
        ResponseEntity.ok(processDetails)
    }


}
