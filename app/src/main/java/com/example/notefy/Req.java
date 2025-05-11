package com.example.notefy;

public class Req {
    private String noteid;
    private String userid;
    private String addednote;

    private String ognote ;

    private String rname ;

    public Req(String noteid, String userid, String addednote, String ognote, String rname) {
        this.noteid = noteid;
        this.userid = userid;
        this.addednote = addednote;
        this.ognote = ognote;
        this.rname = rname;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public String getOgnote() {
        return ognote;
    }

    public void setOgnote(String ognote) {
        this.ognote = ognote;
    }

    public Req(String noteid, String userid, String addednote, String ognote) {
        this.noteid = noteid;
        this.userid = userid;
        this.addednote = addednote;
        this.ognote = ognote;
    }

    public Req(String noteid, String userid, String addednote) {
        this.noteid = noteid;
        this.userid = userid;
        this.addednote = addednote;
    }

    public Req() {
    }

    public String getNoteid() {
        return noteid;
    }

    public void setNoteid(String noteid) {
        this.noteid = noteid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getAddednote() {
        return addednote;
    }

    public void setAddednote(String addednote) {
        this.addednote = addednote;
    }
}
