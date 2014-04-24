package org.shizhz.directive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.shizhz.directive.DirectiveInfo.DirectiveType;
import org.shizhz.exception.UnRecognizedDirectiveException;
import org.shizhz.util.Joiner;

/**
 * This class used to parser the input directive.
 * 
 * @author shizz
 * 
 */
public class DirectiveParser {

    public static DirectiveParser newInstance() {
        return new DirectiveParser();
    }

    /**
     * Parse directive parameters to a String array.
     * 
     * @param directive
     * @return
     */
    private String[] parseParameters(String directive) {
        String[] parts = directive.split(" ");
        if (parts.length > 1) {
            String[] parameters = Joiner.on(" ")
                    .join(Arrays.copyOfRange(parts, 1, parts.length))
                    .split(",");
            List<String> result = new ArrayList<String>();
            for (String param : parameters) {
                if (!"".equals(param.trim())) {
                    result.add(param.trim().toUpperCase());
                }
            }

            return result.toArray(new String[result.size()]);
        }

        return new String[] {};
    }

    private String parseDirectiveName(String directive) {
        return directive.split(" ")[0];
    }

    /**
     * Parse String represented directive to <code>DirectiveInfo</code>.
     * 
     * @param directive
     * @return
     * @throws UnRecognizedDirectiveException
     */
    public DirectiveInfo parse(String directive)
            throws UnRecognizedDirectiveException {
        if (directive == null || "".equals(directive.trim())) {
            return null;
        }

        DirectiveInfo directiveInfo = new DirectiveInfo();
        String directiveName = parseDirectiveName(directive);
        DirectiveType type = null;
        try {
            type = DirectiveInfo.DirectiveType.valueOf(directiveName
                    .toUpperCase());
        } catch (Exception e) {
            throw new UnRecognizedDirectiveException("Unsupported directive : "
                    + directiveName);
        }

        directiveInfo.setDirective(directive);
        directiveInfo.setDirectiveName(directiveName);
        directiveInfo.setDirectiveType(type);
        directiveInfo.setDirectiveParams(parseParameters(directive));

        return directiveInfo;
    }
}
