package lucid7.secufilter.domain;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lucid7.routd.extend.RouteHelper.*;
import lucid7.secufilter.domain.Rule;

import java.util.Collection;


public class UrlInfo {

    private FilterType filterType = null;
    private RuleType ruleType = null;
    private String removeString = null;
    private Multimap<String, Rule> ruleMultimap = null;

    public UrlInfo(final FilterType filterType,
                   final RuleType ruleType,
                   final String removeString) {
        this.filterType = filterType;
        this.ruleType = ruleType;
        this.removeString = removeString;
    }

    public UrlInfo(final FilterType filterType,
                   final String param,
                   final RuleType ruleType,
                   final String removeString) {
        this.filterType = filterType;
        if (ruleMultimap == null) {
            ruleMultimap = ArrayListMultimap.create();
        }
        ruleMultimap.put(param, new Rule(ruleType, removeString));
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public RuleType getRuleType() {
        return ruleType;
    }

    public String getRemoveString() {
        return removeString;
    }

    public void addRule(final String param, final RuleType ruleType, final String removeString) {
        ruleMultimap.put(param, new Rule(ruleType, removeString));
    }

    public Collection<Rule> getRule(final String param) {
         return ruleMultimap.get(param);
    }
}
