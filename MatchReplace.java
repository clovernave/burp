package burp;

public class MatchReplace {

    private final String match;
    private final String replace;
    public int count;

    public MatchReplace(String match, String replace, int count) {
        super();
        this.match = match;
        this.replace = replace;
        this.count = count;
    }

    public String getMatch() {
        return match;
    }

    public String getReplace() {
        return replace;
    }
    public void setCount(int count){
        this.count = count;
    }
    public int getCount(){
        return this.count;
    }

}
