package lucid7.secufilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

public class FilterRequestWrapper extends HttpServletRequestWrapper {

    final private Map<String, String[]> params;

    public FilterRequestWrapper(final HttpServletRequest request) {
        super(request);
        this.params = new HashMap<>(request.getParameterMap());
    }

    public void setParameter(final String name, final String value) {
        setParameter(name, new String[]{value});
    }

    public void setParameter(final String name, final String[] values) {
        params.put(name, values);
    }

    @Override
    public String[] getParameterValues(final String name) {
        String[] temp = params.get(name);
        if (temp == null) {
            return null;
        }
        final int length = temp.length;
        final String[] result = new String[length];
        System.arraycopy(temp, 0, result, 0, length);
        return result;
    }
}
