public class Pair {
    public void generatePngOfHoleAst() {
//        if (classOrInterfaceDeclaration.isInterface()) {
            // test case 1
//            MethodDeclaration mnode = (MethodDeclaration) node;
//            mnode.removeBody();
//            ((MethodDeclaration) node).removeBody();
            // test case 2
//            NodeList<BodyDeclaration<?>> bodyDeclarations = (NodeList<BodyDeclaration<?>>) parent.get().get();
//            bodyDeclarations.add((BodyDeclaration<?>) node);
            // case 3
//            FiledAST fieldAST = new FieldAST();
//            node = fieldAST.generateVariableDeclarationExpr(pattern);
//            node = new FieldAST().generateVariableDeclarationExpr(pattern);
//        }

        // Not supported yet
//        if (firstBodyDeclarationType.equals("MethodDeclaration")
//                && ((MethodDeclaration) bodyDeclaration0).getBody().isEmpty()) {
//            // ((MethodDeclaration) node).removeBody();
//            MethodDeclaration mnode = (MethodDeclaration)node;
//            mnode.removeBody();
//        }

        // test case 1 √
//        while (!parentHole.getHoleType().equals(HoleType.Statements)) {
//            parentHole = parentHole.getParent();
//        }

        // test case 2
//        currentHole.addChild(variableDeclaratorsHole);

        // test case 3 √
//        thenStmt.getClass().toString();

        // test case 4: body emtpy case. √
//        if(a){
//        } else if (b){
//
//        } else if (c){
//
//        }

        // test case "if" 5
//        switch(name){
//            case "if":
//                break;
//        }



        // 嵌套if 6 √
//        if(isTrue()){
//            if (bodyClassStr.equals("ReturnStmt")) {
//                BlockStmt blockStmt = new BlockStmt();
//            } else if (bodyClassStr.equals("BlockStmt")) {
//
//            } else {
//                System.out.println("Should not go to this branch");
//            }
//        } else if (isNotTrue()){
//
//        }

        // case 7
//        exprHole.setHoleTypeOptions(new HoleType[] { HoleType.ImportDeclaration, HoleType.TypeDeclaration });
    }
}
