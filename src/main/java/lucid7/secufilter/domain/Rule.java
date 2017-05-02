package lucid7.secufilter.domain;

import lucid7.routd.extend.RouteHelper.*;

public class Rule {
    private RuleType ruleType;
    private String removeString;

    public Rule(RuleType ruleType, String removeString) {
        this.ruleType = ruleType;
        this.removeString = removeString;
    }

    public RuleType getRuleType() {
        return ruleType;
    }

    public String getRemoveString() {
        return removeString;
    }
}
