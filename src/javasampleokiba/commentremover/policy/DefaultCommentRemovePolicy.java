package javasampleokiba.commentremover.policy;

import java.util.regex.Pattern;

/**
 * デフォルトのコメント削除ポリシークラス.
 */
public class DefaultCommentRemovePolicy implements CommentRemovePolicy {

    @Override
    public String[] getCommentString() {
        return new String[]{"//"};
    }

    @Override
    public String[] getMultiLineCommentBegin() {
        return new String[]{"/*"};
    }

    @Override
    public String[] getMultiLineCommentEnd() {
        return new String[]{"*/"};
    }

    @Override
    public boolean isEnabledRemoveEmptyLine() {
        return true;
    }

    @Override
    public boolean isEnabledRemoveBlankLine() {
        return true;
    }

    @Override
    public Pattern getBlankPattern() {
        return Pattern.compile("\\s*");
    }

    @Override
    public String[] getQuotationMarks() {
        return new String[]{"\""};
    }

    @Override
    public Pattern getRemovePattern() {
        return null;
    }

    @Override
    public Pattern getExceptPattern() {
        return null;
    }
}
