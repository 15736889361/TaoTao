/**
 * Created by fsy on 3/2/16.
 */
public class Login {
    private String openId;
    private String pw;
    private String ts;
    private String un;

    public Login(String openId, String pw, String ts, String un) {
        this.openId = openId;
        this.pw = pw;
        this.ts = ts;
        this.un = un;
    }

    public String getUn() {
        return un;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }
}
