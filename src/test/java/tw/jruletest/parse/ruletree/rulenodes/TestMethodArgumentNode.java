//package tw.jruletest.parse.ruletree.rulenodes;
//
//import org.junit.Assert;
//import org.junit.Test;
//import tw.jruletest.parse.rules.MethodArgumentRule;
//
//public class TestMethodArgumentNode {
//
//    @Test
//    public void testWithStartKeywords() {
//        String code = new MethodArgumentRule().decodeRule("with arguments 2");
//        Assert.assertEquals("2", code);
//    }
//
//    @Test
//    public void testWithStartKeywordsPlusColon() {
//        String code = new MethodArgumentRule().decodeRule("with arguments: 2");
//        Assert.assertEquals("2", code);
//    }
//
//    @Test
//    public void testSingleArgumentWithoutStart() {
//        String code = new MethodArgumentRule().decodeRule("2");
//        Assert.assertEquals("2", code);
//    }
//
//    @Test
//    public void testDoubleArguments() {
//        String code = new MethodArgumentRule().decodeRule("2 and 3");
//        Assert.assertEquals("2, 3", code);
//    }
//
//    @Test
//    public void testTripleArguments() {
//        String code = new MethodArgumentRule().decodeRule("2, 3 and 4");
//        Assert.assertEquals("2, 3, 4", code);
//    }
//
//    @Test
//    public void testTripleArgumentWithMultipleAnds() {
//        String code = new MethodArgumentRule().decodeRule("2 and 3 and 4");
//        Assert.assertEquals("2, 3, 4", code);
//    }
//
//    @Test
//    public void testTripleArgumentWithMultipleCommas() {
//        String code = new MethodArgumentRule().decodeRule("2, 3, 4");
//        Assert.assertEquals("2, 3, 4", code);
//    }
//}