package com.shuvo.ttit.onegpx.login;

public class UserDesignation {

    private String jsm_code;
    private String jsm_name;
    private String jsd_id;
    private String jsd_objective;
    private String dept_name;
    private String div_name;
    private String desg_name;
    private String desg_priority;
    private String joining_date;
    private String div_id;

    public UserDesignation(String jsm_code, String jsm_name, String jsd_id, String jsd_objective, String dept_name, String div_name, String desg_name, String desg_priority, String joining_date, String div_id) {
        this.jsm_code = jsm_code;
        this.jsm_name = jsm_name;
        this.jsd_id = jsd_id;
        this.jsd_objective = jsd_objective;
        this.dept_name = dept_name;
        this.div_name = div_name;
        this.desg_name = desg_name;
        this.desg_priority = desg_priority;
        this.joining_date = joining_date;
        this.div_id = div_id;
    }

    public String getJsm_code() {
        return jsm_code;
    }

    public void setJsm_code(String jsm_code) {
        this.jsm_code = jsm_code;
    }

    public String getJsm_name() {
        return jsm_name;
    }

    public void setJsm_name(String jsm_name) {
        this.jsm_name = jsm_name;
    }

    public String getJsd_id() {
        return jsd_id;
    }

    public void setJsd_id(String jsd_id) {
        this.jsd_id = jsd_id;
    }

    public String getJsd_objective() {
        return jsd_objective;
    }

    public void setJsd_objective(String jsd_objective) {
        this.jsd_objective = jsd_objective;
    }

    public String getDept_name() {
        return dept_name;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }

    public String getDiv_name() {
        return div_name;
    }

    public void setDiv_name(String div_name) {
        this.div_name = div_name;
    }

    public String getDesg_name() {
        return desg_name;
    }

    public void setDesg_name(String desg_name) {
        this.desg_name = desg_name;
    }

    public String getDesg_priority() {
        return desg_priority;
    }

    public void setDesg_priority(String desg_priority) {
        this.desg_priority = desg_priority;
    }

    public String getJoining_date() {
        return joining_date;
    }

    public void setJoining_date(String joining_date) {
        this.joining_date = joining_date;
    }

    public String getDiv_id() {
        return div_id;
    }

    public void setDiv_id(String div_id) {
        this.div_id = div_id;
    }
}
