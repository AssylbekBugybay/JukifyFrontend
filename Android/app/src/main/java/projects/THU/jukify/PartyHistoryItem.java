package projects.THU.jukify;

public class PartyHistoryItem {
    private String id;
    public PartyHistoryItem(String id) {
        this.id = id;
    }

    public String getName() {
        return id;
    }

    @Override
    public String toString() {
        return "PartyHistoryItem{" +
                "name='" + id + '\'' +
                '}';
    }
}
