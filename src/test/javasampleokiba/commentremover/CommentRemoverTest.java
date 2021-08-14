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
 * JUnit5�ɂ��CommentRemover�N���X�̃e�X�g
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
                // �A��
                {"//foo#except//except",        "",                     "//foo#except//except"},
                {"//foo##except##/*except*/",   "",                     "//foo##except##/*except*/"},
                {"//foo\"bar\"'baz'",           "",                     "//foo\"bar\"'baz'"},
                {"/*foo*///bar",                "",                     "/*foo*///bar"},
                {"/*foo*//*bar*/",              "",                     "/*foo*//*bar*/"},
                {"/*foo*/\"bar\"",              "\"bar\"",              "/*foo*/"},
                {"\"foo\"//bar",                "\"foo\"",              "//bar"},
                {"\"foo\"/*bar*/",              "\"foo\"",              "/*bar*/"},
                {"\"foo\"'bar'",                "\"foo\"'bar'",         ""},
                // �l�X�g
                {"/*foo#except//except*/",      "",                     "/*foo#except//except*/"},
                {"/*foo##except##/*bar*/baz*/", "baz*/",                "/*foo##except##/*bar*/"},
                {"/*foo\"baz*/",                "",                     "/*foo\"baz*/"},
                {"\"foo//bar\"",                "\"foo//bar\"",         ""},
                {"\"foo/*bar*/\"",              "\"foo/*bar*/\"",       ""},
                {"\"foo'bar'baz\"",             "\"foo'bar'baz\"",      ""},
                {"'foo\"bar\"baz'",             "'foo\"bar\"baz'",      ""},
                // �󕶎��R�����g
                {"//",                          "",                     "//"},
                {"/****//****//****/",          "",                     "/****//****//****/"},
                // �G�X�P�[�v
                {"\"foo\\\"//bar\"",            "\"foo\\\"//bar\"",     ""},
                {"\"foo\\\\\"//bar\"",          "\"foo\\\\\"",          "//bar\""},
                {"\"foo\\\\\\\"//bar\"",        "\"foo\\\\\\\"//bar\"", ""},
                /*
                 * �R�����g�����񒷂ɂ��D��x�m�F
                 */
                // �P��s�R�����g�� (// <-> ///)
                {"///except",                   "///except",            "///except"},
                // �����s�R�����g�� (/* <-> /**)
                {"/**except**/",                "/**except**/",         "/**except**/"},
                // �P��s�R�����g�ƕ����s�R�����g (# <-> ##)
                {"####foo",                     "foo",                  "####"},
                // �P��s�R�����g�ƕ����s�R�����g ($$ <-> $)
                {"$$foo",                       "",                     "$$foo"},
                // �����s�R�����g�ƈ��p�������� ("" <-> ")
                {"\"\"foo\"\"",                 "",                     "\"\"foo\"\""},
                // �R�����g�J�n������ƃR�����g�J�n�{�I��������(�R�����g���󕶎��̃P�[�X)����������p�^�[��
                // (/** <-> /**/�̏ꍇ�A/**/�̕���D�悷��)
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

        // ��f�[�^�Ŋm�F
        {
            List<String> actual = CommentRemover.remove(new ArrayList<>(), new DefaultCommentRemovePolicy());
            assertEquals(true, actual.isEmpty());
        }

        // ��̓G���[�m�F
        {
            String error = "�R�����g�I�������񂪌�����܂���B";
            try {
                CommentRemover.remove(Arrays.asList("/*foo* /"), new DefaultCommentRemovePolicy());
                fail();
            } catch (ParseException e) {
                assertEquals(error, e.getMessage());
            }
        }
        {
            String error = "�I���̈��p����������܂���B";
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