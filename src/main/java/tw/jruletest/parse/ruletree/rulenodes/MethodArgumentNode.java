package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.exceptions.UnparsableRuleException;
import tw.jruletest.parse.Parser;
import tw.jruletest.parse.ruletree.TreeNode;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodArgumentNode implements TreeNode {
    @Override
    public String generateCode() {
        return null;
    }

    public static int validateRule(String ruleContent) throws InvalidRuleStructureException {
        // Doesn't allow snake case
        Pattern regex = Pattern.compile("^(((?!and)(((-?)[0-9]*(\\.?)[0-9]+)|([a-z][A-Za-z0-9]*)))(((\\sand)|(,))\\s(((-?)[0-9]*(\\.?)[0-9]+)|([a-z][A-Za-z0-9]*)))*)");
        Matcher matcher = regex.matcher(ruleContent);
        //System.out.println("Rule: " + ruleContent);

        // Should be InvalidRuleStructureException???
        if(matcher.find()) {
            //System.out.println("Found match!");
            //System.out.println("Start: " + matcher.start() + ", end: " + matcher.end() + " = " + matcher.group());
            //System.out.println(matcher.group());

            int nextSpaceIndex = 0;
            if(matcher.end() != ruleContent.length()) {
                if(ruleContent.charAt(matcher.end()) != ' ') {
                    nextSpaceIndex = ruleContent.substring(matcher.end()+1).indexOf(" ");
                }
            }

            String requiredRuleSegment = "";
            if(nextSpaceIndex != -1) {
                nextSpaceIndex += matcher.end();
                requiredRuleSegment = ruleContent.substring(0, nextSpaceIndex);
            } else {
                requiredRuleSegment = ruleContent;
            }
            //System.out.println(requiredRuleSegment);

            String[] matchingWords = requiredRuleSegment.split(" ");
            String lastWord = matchingWords[matchingWords.length-1];

            if(Parser.KEYWORDS.contains(lastWord)) {
                matchingWords = requiredRuleSegment.substring(0, requiredRuleSegment.indexOf(lastWord)-1).split(" ");
                lastWord = matchingWords[matchingWords.length-1];
                if(lastWord.equals("and") && matchingWords.length > 1) {
                    String penultimateWord = matchingWords[matchingWords.length-2];
                    if(penultimateWord.equals("and") || (penultimateWord.charAt(penultimateWord.length()-1) == ',') || !isAlphaNumeric(penultimateWord)) {
                        //System.out.println("Error detected");
                        throw new InvalidRuleStructureException(ruleContent, "Method Argument Node");
                    } else {
                        //System.out.println("Allowed rule");
                        //System.out.println(ruleContent.substring(0, requiredRuleSegment.lastIndexOf("and") - 1));
                        return requiredRuleSegment.lastIndexOf("and") - 1;
                    }
                } else {
                    //System.out.println("Error detected");
                    throw new InvalidRuleStructureException(ruleContent, "Method Argument Node");
                }
            } else if(lastWord.equals("and") || (lastWord.charAt(lastWord.length()-1) == ',') || !isAlphaNumeric(lastWord)) {
                // InvalidStructureException
                //System.out.println("Error detected");
                throw new InvalidRuleStructureException(ruleContent, "Method Argument Node");
            } else {
                if(requiredRuleSegment.length() == ruleContent.length()) {
                    //System.out.println("Allowed rule");
                    //System.out.println(requiredRuleSegment);
                    return requiredRuleSegment.length();
                } else {
                    int currentSegmentLength = requiredRuleSegment.length() + 1;
                    String[] nextWords = ruleContent.substring(currentSegmentLength).split(" ");
                    if(nextWords.length == 1) {
                        // Can't be valid
                        //System.out.println("Error detected");
                        throw new InvalidRuleStructureException(ruleContent, "Method Argument Node");
                    } else {
                        if(nextWords[0].equals("and") && (isAlphaNumeric(nextWords[1]) || Parser.KEYWORDS.contains(nextWords[1]))) {
                            //System.out.println("Allowed rule");
                            //System.out.println(ruleContent.indexOf(nextWords[1]) + nextWords[1].length() + 1);
                            //System.out.println(ruleContent.substring(0, ruleContent.indexOf(nextWords[1]) + nextWords[1].length() + 1));
                            return ruleContent.indexOf(nextWords[1]) + nextWords[1].length() + 1;
                        } else if(nextWords[0].equals("in") && isAlphaNumeric(nextWords[1])) {
                            //System.out.println("Allowed rule");
                            //System.out.println(requiredRuleSegment);
                            return requiredRuleSegment.length();
                        }else {
                            //System.out.println("Detected error");
                            throw new InvalidRuleStructureException(ruleContent, "Method Argument Node");
                        }
                    }
                }
            }
        } else {
            //System.out.println("No match found");
            throw new InvalidRuleStructureException(ruleContent, "Method Argument Node");
        }
    }

    private static boolean isAlphaNumeric(String segment) {
        return Pattern.compile("((-?)[0-9]*(\\.?)[0-9]+)|([a-z][a-zA-z0-9]*)").matcher(segment).matches();
    }

//    public static void main(String[] args) {
//        // All work properly
//        validateRule("value1, 10.5 and value3");
//        System.out.println();
//        validateRule("1v, value2, value3");
//        System.out.println();
//        validateRule("value1 and store dummy");
//        System.out.println();
//        validateRule("value1, value2,");
//        System.out.println();
//        validateRule("value1, value2, 4");
//        System.out.println();
//        validateRule("value1, value2 value3");
//        System.out.println();
//        validateRule("value1, value2 value3 value4");
//        System.out.println();
//        validateRule("value1 and -65.78 and value3");
//        System.out.println();
//        validateRule("and value1 and -65.78 and value3");
//        System.out.println();
//        validateRule("value1 and value2 and");
//        System.out.println();
//        validateRule("value1, value2 and and");
//        System.out.println();
//        validateRule("v, and !");
//        System.out.println();
//        validateRule("10.5");
//        System.out.println();
//        validateRule("-100");
//    }
}
