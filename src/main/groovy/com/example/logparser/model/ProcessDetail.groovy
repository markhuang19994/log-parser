package com.example.logparser.model

class ProcessDetail {
    private String processId
    private String userId
    private List<LogDetail> logDetails

    ProcessDetail(String processId, String userId, List<LogDetail> logDetails) {
        this.processId = processId
        this.userId = userId
        this.logDetails = logDetails
    }

    String getProcessId() {
        return processId
    }

    void setProcessId(String processId) {
        this.processId = processId
    }

    String getUserId() {
        return userId
    }

    void setUserId(String userId) {
        this.userId = userId
    }

    List<LogDetail> getLogDetail() {
        return logDetails
    }

    void setLogDetail(List<LogDetail> logDetails) {
        this.logDetails = logDetails
    }
}
