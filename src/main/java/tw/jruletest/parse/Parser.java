package tw.jruletest.parse;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.exceptions.UnparsableRuleException;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.parse.ruletree.rulenodes.*;

import java.util.*;

/**
 * Parses the rules extracted from test classes to generate the code required to create test suites.
 *
 * @author Toby Wride
 */

public class Parser {

    /**
     * Constant list of Strings storing the keywords required to detect new rules.
     * */

    public static final ArrayList<String> KEYWORDS = new ArrayList<>(Arrays.asList("call", "get", "store", "expect"));
    private static final ArrayList<String> POSSIBLE_CONNECTIVES = new ArrayList<>(Arrays.asList(",", "and", "then"));

    private static LinkedList<TreeNode> rules = new LinkedList<>();

    /**
     * Decodes and generates the code from a set o rules
     *
     * @param rules list of rules to generate code for
     *
     * @return the code generated from the list of rules.
     * */

    public static String parseRules(String[] rules) {
        String generatedCode = "";
        for(String rule: rules) {
            generatedCode += parseRule(rule);
        }
        return generatedCode;
    }

    /**
     * Decodes and generates the code from a given rule
     *
     * @param rule the rule to generate code for
     *
     * @return the code generated from the given rule.
     * */

    public static String parseRule(String rule) {
        String[] subRules = rule.split("\\.\\s");
        String codeBlock = "";
        for(String subRule: subRules) {
            try {
                generateTrees(subRule);
                for (TreeNode ruleNode : rules) {
                    codeBlock += ruleNode.generateCode() + "\n";
                }
            } catch(UnparsableRuleException e) {
                e.printError();
            }
            rules = new LinkedList<>();
        }
        return codeBlock;
    }

    /**
     * Breaks up the provided rule into separate rules and generates the rule tree required to validate the rule and generate the corresponding code.
     *
     * @param rule the rule to segment and generate the rule tree for
     *
     * @return the list of validated rule segments
     *
     * @throws UnparsableRuleException thrown if the rule could be validated or could not generate valid code
     * */

    public static ArrayList<String> generateTrees(String rule) throws UnparsableRuleException {
        String remainingRule = rule.trim();
        ArrayList<String> subRules = new ArrayList<>();

        // TODO Get first command, use it in case of non-existent subsequent keywords
        do {
            int subRuleIndex = getEndOfSubRule(remainingRule, remainingRule.split(" ")[0]);
            subRules.add(remainingRule.substring(0, subRuleIndex));
            remainingRule = remainingRule.substring(subRuleIndex).trim();

            String connective = "";
            for(String possibleConnective: POSSIBLE_CONNECTIVES) {
                if(remainingRule.startsWith(possibleConnective)) {
                    connective = possibleConnective;
                }
            }

            if(!connective.isEmpty()) {
                remainingRule = remainingRule.substring(connective.length()).trim();

                if(remainingRule.isEmpty()) {
                    throw new UnparsableRuleException(remainingRule);
                }
            } else if(!remainingRule.isEmpty()) {
                throw new UnparsableRuleException(remainingRule);
            }
        } while(!remainingRule.isEmpty());

        return subRules;
    }

    private static int getEndOfSubRule(String rule, String startCommand) throws UnparsableRuleException {
        TreeNode node;
        try {
            switch (startCommand.toLowerCase()) {
                case "expect":
                    node = new ExpectationNode();
                    break;
                case "get":
                    node = new GetValueNode();
                    break;
                case "store":
                    node = new StoreValueNode();
                    break;
                case "call":
                    node = new CallMethodNode();
                    break;
                default:
                    throw new UnparsableRuleException(rule);
            }
            rules.add(node);
            return node.validateRule(rule);
        } catch(InvalidRuleStructureException e) {
            e.printError();
            throw new UnparsableRuleException(rule);
        }
    }
}