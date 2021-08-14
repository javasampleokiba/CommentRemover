package test.javasampleokiba.commentremover;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import javasampleokiba.commentremover.CommentRemover;
import javasampleokiba.commentremover.policy.CCommentRemovePolicy;
import javasampleokiba.commentremover.policy.CSharpCommentRemovePolicy;
import javasampleokiba.commentremover.policy.CplusplusCommentRemovePolicy;
import javasampleokiba.commentremover.policy.CustomCommentRemovePolicy;
import javasampleokiba.commentremover.policy.DefaultCommentRemovePolicy;
import javasampleokiba.commentremover.policy.CommentRemovePolicy;
import javasampleokiba.commentremover.policy.JavaCommentRemovePolicy;
import javasampleokiba.commentremover.policy.JavaScriptCommentRemovePolicy;
import javasampleokiba.commentremover.policy.PerlCommentRemovePolicy;
import javasampleokiba.commentremover.policy.PhpCommentRemovePolicy;
import javasampleokiba.commentremover.policy.PythonCommentRemovePolicy;
import javasampleokiba.commentremover.policy.RubyCommentRemovePolicy;
import javasampleokiba.commentremover.policy.VBCommentRemovePolicy;

/**
 * JUnit5によるCommentRemoverクラスのテスト
 */
public class CommentRemoverTest {

    @Test
    public void test() throws IOException, ParseException {
        CustomCommentRemovePolicy policy = new CustomCommentRemovePolicy(new DefaultCommentRemovePolicy())
            .setCommentString(new String[] {"#", "//", "///", "$$"})
            .setMultiLineCommentBegin(new String[] {"/*", "/**", "##", "\"\"", "$"})
            .setMultiLineCommentEnd(new String[] {"*/", "**/", "##", "\"\"", "$"})
            .setEnabledRemoveEmptyLine(false)
            .setEnabledRemoveBlankLine(false)
            .setQuotationMarks(new String[]{"\"", "'"})
            .setExceptPattern(Pattern.compile("^except$"));

        String[][] testPatterns = {
                // 連続
                {"//foo#except//except",        "",                     "//foo#except//except"},
                {"//foo##except##/*except*/",   "",                     "//foo##except##/*except*/"},
                {"//foo\"bar\"'baz'",           "",                     "//foo\"bar\"'baz'"},
                {"/*foo*///bar",                "",                     "/*foo*///bar"},
                {"/*foo*//*bar*/",              "",                     "/*foo*//*bar*/"},
                {"/*foo*/\"bar\"",              "\"bar\"",              "/*foo*/"},
                {"\"foo\"//bar",                "\"foo\"",              "//bar"},
                {"\"foo\"/*bar*/",              "\"foo\"",              "/*bar*/"},
                {"\"foo\"'bar'",                "\"foo\"'bar'",         ""},
                // ネスト
                {"/*foo#except//except*/",      "",                     "/*foo#except//except*/"},
                {"/*foo##except##/*bar*/baz*/", "baz*/",                "/*foo##except##/*bar*/"},
                {"/*foo\"baz*/",                "",                     "/*foo\"baz*/"},
                {"\"foo//bar\"",                "\"foo//bar\"",         ""},
                {"\"foo/*bar*/\"",              "\"foo/*bar*/\"",       ""},
                {"\"foo'bar'baz\"",             "\"foo'bar'baz\"",      ""},
                {"'foo\"bar\"baz'",             "'foo\"bar\"baz'",      ""},
                // 空文字コメント
                {"//",                          "",                     "//"},
                {"/****//****//****/",          "",                     "/****//****//****/"},
                // エスケープ
                {"\"foo\\\"//bar\"",            "\"foo\\\"//bar\"",     ""},
                {"\"foo\\\\\"//bar\"",          "\"foo\\\\\"",          "//bar\""},
                {"\"foo\\\\\\\"//bar\"",        "\"foo\\\\\\\"//bar\"", ""},
                /*
                 * コメント文字列長による優先度確認
                 */
                // 単一行コメント内 (// <-> ///)
                {"///except",                   "///except",            "///except"},
                // 複数行コメント内 (/* <-> /**)
                {"/**except**/",                "/**except**/",         "/**except**/"},
                // 単一行コメントと複数行コメント (# <-> ##)
                {"####foo",                     "foo",                  "####"},
                // 単一行コメントと複数行コメント ($$ <-> $)
                {"$$foo",                       "",                     "$$foo"},
                // 複数行コメントと引用符文字列 ("" <-> ")
                {"\"\"foo\"\"",                 "",                     "\"\"foo\"\""},
                // コメント開始文字列とコメント開始＋終了文字列(コメントが空文字のケース)が競合するパターン
                // (/** <-> /**/の場合、/**/の方を優先する)
                {"/**/**/",                     "**/",                  "/**/"},
        };
        for (String[] testPattern : testPatterns) {
            try {
                List<String> actual = CommentRemover.remove(Arrays.asList(testPattern[0]), policy);
                assertEquals(testPattern[1], actual.get(0));
                actual = CommentRemover.extract(Arrays.asList(testPattern[0]), policy);
                assertEquals(testPattern[2], actual.get(0));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // 空データで確認
        {
            List<String> actual = CommentRemover.remove(new ArrayList<>(), new DefaultCommentRemovePolicy());
            assertEquals(true, actual.isEmpty());
        }

        // 解析エラー確認
        {
            String error = "コメント終了文字列が見つかりません。";
            try {
                CommentRemover.remove(Arrays.asList("/*foo* /"), new DefaultCommentRemovePolicy());
                fail();
            } catch (ParseException e) {
                assertEquals(error, e.getMessage());
            }
        }
        {
            String error = "終了の引用符が見つかりません。";
            try {
                CommentRemover.remove(Arrays.asList("\"foo"), new DefaultCommentRemovePolicy());
                fail();
            } catch (ParseException e) {
                assertEquals(error, e.getMessage());
            }
        }
    }

    @Test
    public void testFile() throws IOException {
        String dir = "test_resource\\";
        Object[][] patterns = {
                {"C.txt", new CCommentRemovePolicy()},
                {"C++.txt", new CplusplusCommentRemovePolicy()},
                {"C#.txt", new CSharpCommentRemovePolicy()},
                {"Java.txt", new JavaCommentRemovePolicy()},
                {"JavaScript.txt", new JavaScriptCommentRemovePolicy()},
                {"Perl.txt", new PerlCommentRemovePolicy()},
                {"Php.txt", new PhpCommentRemovePolicy()},
                {"Python.txt", new PythonCommentRemovePolicy()},
                {"Ruby.txt", new RubyCommentRemovePolicy()},
                {"VB.txt", new VBCommentRemovePolicy()},
        };
        for (Object[] pattern : patterns) {
            if (!Paths.get(dir + pattern[0]).toFile().exists()) {
                continue;
            }
            List<String> lines = Files.readAllLines(Paths.get(dir + pattern[0]));
            CustomCommentRemovePolicy policy = new CustomCommentRemovePolicy((CommentRemovePolicy) pattern[1]);
            List<String> result;
            try {
                result = CommentRemover.remove(lines, policy);
                Files.write(Paths.get(dir + pattern[0] + "_2"), result,
                        StandardOpenOption.WRITE, StandardOpenOption.CREATE);

                result = CommentRemover.extract(lines, policy);
                Files.write(Paths.get(dir + pattern[0] + "_3"), result,
                        StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
