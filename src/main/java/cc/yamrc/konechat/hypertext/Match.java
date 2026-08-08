package cc.yamrc.konechat.hypertext;

import java.util.Map;

public final class Match {
    public final int start;
    public final int end;
    public final Map<String, String> groups;
    public final Object data;

    public Match(int start, int end, Map<String, String> groups, Object data) {
        if (start < 0 || end <= start) throw new IllegalArgumentException("match range is invalid");
        this.start = start;
        this.end = end;
        this.groups = Map.copyOf(groups == null ? Map.of() : groups);
        this.data = data;
    }

    public int getStart() { return start; }
    public int getEnd() { return end; }
    public Map<String, String> getGroups() { return groups; }
    public Object getData() { return data; }

    public String text(String source) {
        return source.substring(start, end);
    }
}
