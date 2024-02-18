package tw.jruletest.parse.ruletree.innernodes.expressionnodes.booleannodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.Rule;
import tw.jruletest.parse.ruletree.RuleNode;
import tw.jruletest.parse.ruletree.innernodes.ChildNode;

import java.lang.reflect.Type;

public class LogicalComparisonNode extends ChildNode implements Rule {

    private final String[] POSSIBLE_COMPARATORS = {" less than or equal to ", " less than ", "greater than or equal to ",
                                                    " greater than ", " not equal to ", " equal to "};

    private RuleNode firstComparisonArgument;
    private RuleNode secondComparisonArgument;

    private String comparator;

    @Override
    public String generateCode() {
        String code = "(" + ((Rule)firstComparisonArgument).generateCode();
        switch(comparator.trim()) {
            case "equal to":
                code += " == ";
                break;
            case "not equal to": case "does not equal":
                code += " != ";
                break;
            case "less than":
                code += " < ";
                break;
            case "less than or equal to":
                code += " <= ";
                break;
            case "greater than":
                code += " > ";
                break;
            case "greater than or equal to":
                code += " >= ";
                break;
        }
        return code + ((Rule)secondComparisonArgument).generateCode() + ")";
    }

    @Override
    public void validateRule(String ruleContent) throws InvalidRuleStructureException {
        int comparatorIndex = ruleContent.length();
        for(String comparator: POSSIBLE_COMPARATORS) {
            int newIndex = ruleContent.indexOf(comparator);
            if((newIndex >= 0) && (newIndex < comparatorIndex)) {
                comparatorIndex = newIndex;
                this.comparator = comparator;
            }
        }

        String comparatorPhrase = " is" + comparator;
        int phraseIndex = ruleContent.indexOf(comparatorPhrase);
        if(phraseIndex == -1) {
            throw new InvalidRuleStructureException(ruleContent, "Logical Comparison Node");
        }

        String firstSegment = ruleContent.substring(0, phraseIndex);
        String secondSegment = ruleContent.substring(phraseIndex + comparatorPhrase.length());

        firstComparisonArgument = RuleNode.getChildNode(firstSegment, RuleNode.OPERABLE_NODE);
        secondComparisonArgument = RuleNode.getChildNode(secondSegment, RuleNode.OPERABLE_NODE);
        int firstArgumentIndex = firstComparisonArgument.getEndIndex();
        int secondArgumentIndex = secondComparisonArgument.getEndIndex();

        if(firstArgumentIndex != phraseIndex) {
            throw new InvalidRuleStructureException(ruleContent, "Logical Comparison Node");
        } else {
            endIndex = comparatorIndex + comparator.length() + secondArgumentIndex;
        }
    }

    @Override
    public Type getType() {
        return boolean.class;
    }
}
