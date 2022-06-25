public class Pair {
    public void generatePngOfHoleAst() {
        HoleNode exprHole = new HoleNode(HoleType.Undefined, true);
        if (holeType.equals(HoleType.Parameters)) {
            currentHole.set(HoleType.Parameters, false);
            parentHole.addChild(exprHole);
        } else if (holeType.equals(HoleType.ForInitialization)) {
            currentHole.set(HoleType.ForInitialization, false);
            exprHole.setHoleTypeOptionsOfOnlyOne(HoleType.ForCompare);
            parentHole.addChild(exprHole);
        } else {
            parentHole.deleteHole(holeIndex);
            parentOfParentHole.addChild(exprHole);
        }
    }
}
