package org.shizhz.directive;

public class DirectiveInfo {

    public enum DirectiveType {
        G("Add Route to Graph"), D("Distance"), TS("Trips with Stops"), TMS(
                "Trips with Max Stops"), TLTD("Trips Less Than Distance"), SD(
                "Shortest Distance"), PRINT("Print Route Network"), HELP(
                "Usage Information");

        private String desc;

        DirectiveType(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    private String directive;

    private DirectiveType directiveType;

    private String directiveName;

    private String[] directiveParams;

    public String getDirective() {
        return directive;
    }

    public void setDirective(String directive) {
        this.directive = directive;
    }

    public DirectiveType getDirectiveType() {
        return directiveType;
    }

    public void setDirectiveType(DirectiveType directiveType) {
        this.directiveType = directiveType;
    }

    public String getDirectiveName() {
        return directiveName;
    }

    public void setDirectiveName(String directiveName) {
        this.directiveName = directiveName;
    }

    public String[] getDirectiveParams() {
        return directiveParams;
    }

    public void setDirectiveParams(String[] directiveParams) {
        this.directiveParams = directiveParams;
    }
}
