package com.alipay.sofa.doc.model;

import java.util.List;

/**
 * @author <a href=mailto:zhanggeng.zg@antfin.com>GengZhang</a>
 */
public class DRMSyncResult {
    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 消息
     */
    private String message;
    /**
     * 成功列表
     */
    private List<String> successes;
    /**
     * 失败列表
     */
    private List<String> failures;
    /**
     * 处理的服务器ip，方便查看日志
     */
    private String ip;

    public boolean isSuccess() {
        return success;
    }

    public DRMSyncResult setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public DRMSyncResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public List<String> getSuccesses() {
        return successes;
    }

    public DRMSyncResult setSuccesses(List<String> successes) {
        this.successes = successes;
        return this;
    }

    public List<String> getFailures() {
        return failures;
    }

    public DRMSyncResult setFailures(List<String> failures) {
        this.failures = failures;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public DRMSyncResult setIp(String ip) {
        this.ip = ip;
        return this;
    }

    @Override
    public String toString() {
        return "DRMSyncResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", successes=" + successes +
                ", failures=" + failures +
                ", ip='" + ip + '\'' +
                '}';
    }
}
