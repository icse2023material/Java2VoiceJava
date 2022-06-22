public class ListHelper {
    public Pair<List<Unit>, List<Unit>> splitList(List<Unit> units, String splitAt) {
        List<Unit> left = new ArrayList<Unit>();
        return new Pair<List<Unit>, List<Unit>>(left, units);
    }
}
