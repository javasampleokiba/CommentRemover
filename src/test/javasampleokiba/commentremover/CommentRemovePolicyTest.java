package test.javasampleokiba.commentremover;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import javasampleokiba.commentremover.CommentRemover;
import javasampleokiba.commentremover.policy.CustomCommentRemovePolicy;
import javasampleokiba.commentremover.policy.DefaultCommentRemovePolicy;
import javasampleokiba.commentremover.policy.CommentRemovePolicy;

/**
 * JUnit5によるCommentRemovePolicyクラスのテスト
 */
public class CommentRemovePolicyTest {

    @Test
    public void testCheckPolicy() throws ParseException {
        List<String> lines = new ArrayList<>();
        {
            String error = "コメント開始文字列とコメント終了文字列の片方だけを有効にすることはできません。";
            CommentRemovePolicy[] policies = {
                new DefaultCommentRemovePolicy() {
                    public String[] getMultiLineCommentBegin() { return new String[] {"/*"}; }
                    public String[] getMultiLineCommentEnd() { return null; }
                },
                new DefaultCommentRemovePolicy() {
                    public String[] getMultiLineCommentBegin() { return null; }
                    public String[] getMultiLineCommentEnd() { return new String[] {"/*"}; }
                },
                new DefaultCommentRemovePolicy() {
                    public String[] getMultiLineCommentBegin() { return new String[] {"/*"}; }
                    public String[] getMultiLineCommentEnd() { return new String[] {""}; }
                },
                new DefaultCommentRemovePolicy() {
                    public String[] getMultiLineCommentBegin() { return new String[] {""}; }
                    public String[] getMultiLineCommentEnd() { return new String[] {"/*"}; }
                },
                new DefaultCommentRemovePolicy() {
                    public String[] getMultiLineCommentBegin() { return new String[] {"/*"}; }
                    public String[] getMultiLineCommentEnd() { return new String[] {null}; }
                },
                new DefaultCommentRemovePolicy() {
                    public String[] getMultiLineCommentBegin() { return new String[] {null}; }
                    public String[] getMultiLineCommentEnd() { return new String[] {"/*"}; }
                },
            };
            for (CommentRemovePolicy policy : policies) {
                try {
                    CommentRemover.remove(lines, policy);
                    fail();
                } catch (IllegalArgumentException e) {
                    assertEquals(error, e.getMessage());
                }
            }
        }
        {
            String error = "コメント開始文字列とコメント終了文字列の数が異なります。";
            CommentRemovePolicy[] policies = {
                new DefaultCommentRemovePolicy() {
                    public String[] getMultiLineCommentBegin() { return new String[] {"/*"}; }
                    public String[] getMultiLineCommentEnd() { return new String[] {"*/", "*/"}; }
                },
            };
            for (CommentRemovePolicy policy : policies) {
                try {
                    CommentRemover.remove(lines, policy);
                    fail();
                } catch (IllegalArgumentException e) {
                    assertEquals(error, e.getMessage());
                }
            }
        }
    }

    @Test
    public void testCommentString() throws ParseException {
        CommentRemovePolicy[] policies = {
            new DefaultCommentRemovePolicy() {
                public String[] getCommentString() { return null; }
            },
        };
        String line = "/*foo*///bar";
        for (CommentRemovePolicy policy : policies) {
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policy);
            assertEquals("//bar", actual.get(0));
        }
    }

    @Test
    public void testMultiLineComment() throws ParseException {
        CommentRemovePolicy[] policies = {
            new DefaultCommentRemovePolicy() {
                public String[] getMultiLineCommentBegin() { return null; }
                public String[] getMultiLineCommentEnd() { return null; }
            },
            new DefaultCommentRemovePolicy() {
                public String[] getMultiLineCommentBegin() { return new String[] {}; }
                public String[] getMultiLineCommentEnd() { return new String[] {}; }
            },
            new DefaultCommentRemovePolicy() {
                public String[] getMultiLineCommentBegin() { return new String[] {null}; }
                public String[] getMultiLineCommentEnd() { return new String[] {null}; }
            },
            new DefaultCommentRemovePolicy() {
                public String[] getMultiLineCommentBegin() { return new String[] {""}; }
                public String[] getMultiLineCommentEnd() { return new String[] {""}; }
            },
        };
        String line = "/*foo*///bar";
        for (CommentRemovePolicy policy : policies) {
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policy);
            assertEquals("/*foo*", actual.get(0));
        }
    }

    @Test
    public void testEnabledRemoveEmptyLine() throws ParseException {
        CommentRemovePolicy[] policies = {
            new DefaultCommentRemovePolicy() {
                public boolean isEnabledRemoveEmptyLine() { return true; }
                public boolean isEnabledRemoveBlankLine() { return false; }
            },
            new DefaultCommentRemovePolicy() {
                public boolean isEnabledRemoveEmptyLine() { return false; }
                public boolean isEnabledRemoveBlankLine() { return false; }
            },
        };
        {
            String line = "//foo";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policies[0]);
            assertEquals(true, actual.isEmpty());
        }
        {
            String line = " //foo";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policies[0]);
            assertEquals(" ", actual.get(0));
        }
        {
            String line = "";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policies[0]);
            assertEquals("", actual.get(0));
        }
        {
            String line = "//foo";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policies[1]);
            assertEquals("", actual.get(0));
        }
        {
            String line = " //foo";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policies[1]);
            assertEquals(" ", actual.get(0));
        }
        {
            String line = "";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policies[1]);
            assertEquals("", actual.get(0));
        }
    }

    @Test
    public void testEnabledRemoveBlankLine() throws ParseException {
        CommentRemovePolicy[] policies = {
            new DefaultCommentRemovePolicy() {
                public boolean isEnabledRemoveBlankLine() { return true; }
            },
            new DefaultCommentRemovePolicy() {
                public boolean isEnabledRemoveBlankLine() { return false; }
            },
        };
        {
            String line = " \t/*foo*/ \t";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policies[0]);
            assertEquals(true, actual.isEmpty());
        }
        {
            String line = " \t/*foo*/　\t";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policies[0]);
            assertEquals(" \t　\t", actual.get(0));
        }
        {
            String line = " \t \t";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policies[0]);
            assertEquals(" \t \t", actual.get(0));
        }
        {
            String line = " \t/*foo*/ \t";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policies[1]);
            assertEquals(" \t \t", actual.get(0));
        }
        {
            String line = " \t/*foo*/　\t";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policies[1]);
            assertEquals(" \t　\t", actual.get(0));
        }
        {
            String line = " \t \t";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policies[1]);
            assertEquals(" \t \t", actual.get(0));
        }
    }

    @Test
    public void testBlankPattern() throws ParseException {
        CustomCommentRemovePolicy policy = new CustomCommentRemovePolicy(new DefaultCommentRemovePolicy())
                .setBlankPattern(Pattern.compile("[\\s_]*"));
        {
            String line = "_ \t_/*foo*/_ \t_";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policy);
            assertEquals(true, actual.isEmpty());
        }
        {
            String line = "_ \t_/*foo*/_　\\t_";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policy);
            assertEquals("_ \t__　\\t_", actual.get(0));
        }
        {
            String line = "_ \t__ \\t_";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policy);
            assertEquals("_ \t__ \\t_", actual.get(0));
        }
    }

    @Test
    public void testQuotationMarks() throws ParseException {
        {
            CustomCommentRemovePolicy policy = new CustomCommentRemovePolicy(new DefaultCommentRemovePolicy())
                    .setQuotationMarks(new String[]{"\"", "|"});
            String line = "|//foo\"|//bar";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policy);
            assertEquals("|//foo\"|", actual.get(0));
        }
        {
            CustomCommentRemovePolicy policy = new CustomCommentRemovePolicy(new DefaultCommentRemovePolicy())
                    .setQuotationMarks(null);
            String line = "\"//foo\"//bar";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policy);
            assertEquals("\"", actual.get(0));
        }
    }

    @Test
    public void testRemovePattern() throws ParseException {
        CustomCommentRemovePolicy policy = new CustomCommentRemovePolicy(new DefaultCommentRemovePolicy())
            .setMultiLineCommentBegin(new String[] {"/*", "/**"})
            .setMultiLineCommentEnd(new String[] {"*/", "*/"})
            .setRemovePattern(Pattern.compile("^[0-9]+"));
        {
            String line = "// 12345";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policy);
            assertEquals("// 12345", actual.get(0));
        }
        {
            String line = "//12345 ";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policy);
            assertEquals(true, actual.isEmpty());
        }
        {
            String line = "/*12345 */";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policy);
            assertEquals(true, actual.isEmpty());
        }
        {
            String line = "/**12345 */";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policy);
            assertEquals(true, actual.isEmpty());
        }
        {
            List<String> lines = new ArrayList<>();
            lines.add("/*12345 ");
            lines.add("  foo ");
            lines.add("  bar */");
            List<String> actual = CommentRemover.remove(lines, policy);
            assertEquals(true, actual.isEmpty());
        }
        {
            List<String> lines = new ArrayList<>();
            lines.add("/*foo ");
            lines.add("12345 ");
            lines.add("  bar */");
            List<String> actual = CommentRemover.remove(lines, policy);
            assertEquals(true, actual.isEmpty());
        }
        {
            List<String> lines = new ArrayList<>();
            lines.add("/* 12345*//*foo ");
            lines.add("  bar ");
            lines.add("12345 */// 12345");
            List<String> actual = CommentRemover.remove(lines, policy);
            assertEquals("/* 12345*/", actual.get(0));
            assertEquals("// 12345", actual.get(1));
        }
        {
            List<String> lines = new ArrayList<>();
            lines.add("/*foo ");
            lines.add("  bar ");
            lines.add(" 12345*/");
            List<String> actual = CommentRemover.remove(lines, policy);
            assertEquals("/*foo ", actual.get(0));
            assertEquals("  bar ", actual.get(1));
            assertEquals(" 12345*/", actual.get(2));
        }
    }

    @Test
    public void testExceptPattern() throws ParseException {
        CustomCommentRemovePolicy policy = new CustomCommentRemovePolicy(new DefaultCommentRemovePolicy())
                .setExceptPattern(Pattern.compile("^[0-9]+"));
        {
            String line = "// 12345";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policy);
            assertEquals(true, actual.isEmpty());
        }
        {
            String line = "//12345 ";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policy);
            assertEquals("//12345 ", actual.get(0));
        }
        {
            String line = "/*12345 */";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policy);
            assertEquals("/*12345 */", actual.get(0));
        }
        {
            List<String> lines = new ArrayList<>();
            lines.add("/*12345 ");
            lines.add("  foo ");
            lines.add("  bar */");
            List<String> actual = CommentRemover.remove(lines, policy);
            assertEquals("/*12345 ", actual.get(0));
            assertEquals("  foo ", actual.get(1));
            assertEquals("  bar */", actual.get(2));
        }
        {
            List<String> lines = new ArrayList<>();
            lines.add("/*foo ");
            lines.add("12345 ");
            lines.add("  bar */");
            List<String> actual = CommentRemover.remove(lines, policy);
            assertEquals("/*foo ", actual.get(0));
            assertEquals("12345 ", actual.get(1));
            assertEquals("  bar */", actual.get(2));
        }
        {
            List<String> lines = new ArrayList<>();
            lines.add("/*foo ");
            lines.add("  bar ");
            lines.add("12345 */");
            List<String> actual = CommentRemover.remove(lines, policy);
            assertEquals("/*foo ", actual.get(0));
            assertEquals("  bar ", actual.get(1));
            assertEquals("12345 */", actual.get(2));
        }
        {
            List<String> lines = new ArrayList<>();
            lines.add("/*12345*//*foo ");
            lines.add("  bar ");
            lines.add(" 12345*///12345");
            List<String> actual = CommentRemover.remove(lines, policy);
            assertEquals("/*12345*/", actual.get(0));
            assertEquals("//12345", actual.get(1));
        }

        policy.setRemovePattern(Pattern.compile("0{5}"));
        {
            String line = "//00000";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policy);
            assertEquals(true, actual.isEmpty());
        }
        {
            String line = "// 12345";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policy);
            assertEquals("// 12345", actual.get(0));
        }
        {
            String line = "//12345";
            List<String> actual = CommentRemover.remove(Arrays.asList(line), policy);
            assertEquals("//12345", actual.get(0));
        }
    }
}
