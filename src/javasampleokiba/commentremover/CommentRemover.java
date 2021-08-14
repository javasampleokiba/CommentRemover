package javasampleokiba.commentremover;

import java.text.ParseException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javasampleokiba.commentremover.parser.CodeParser;
import javasampleokiba.commentremover.parser.CodePosition;
import javasampleokiba.commentremover.policy.CustomCommentRemovePolicy;
import javasampleokiba.commentremover.policy.CommentRemovePolicy;

/**
 * �\�[�X�R�����g�̈ꊇ�폜����ђ��o���s���N���X.
 *
 * <p>[��̓A���S���Y��]</p>
 * <ul>
 * <li>�R�����g�̊J�n��\��������i�ȉ��A�R�����g�J�n������j���s�̒��Ō��o���ꂽ�ꍇ�ɁA
 * ���̕���������̕����i�����s�R�����g�̏ꍇ�͎��̍s�ȍ~���j���R�����g�Ƃ݂Ȃ��܂��B</li>
 * <li>1�s�ɕ����̃R�����g�J�n�����񂪑��݂���ꍇ�́A�ł��擪�̂��̂��R�����g�J�n������Ɖ��߂��܂��B</li>
 * <li>�R�����g�J�n�����񂪕�����`����Ă���ꍇ�́A�Œ��}�b�`���D��ł��B�Ⴆ�΁A/**��/*�����D�悵�Č��o����܂��B</li>
 * <li>�R�����g�̊J�n�܂��͏I����\��������i�ȉ��A�R�����g������j�͑啶������������ʂ���܂��B</li>
 * <li>���p���ň͂܂ꂽ���e���������񒆂Ɍ����R�����g������́A�R�����g������Ƃ݂Ȃ�����������܂��B</li>
 * <li>���e���������񒆂̃o�b�N�X���b�V���ɂ����p���̃G�X�P�[�v�ɑΉ����Ă��܂��B</li>
 * <li>���ꖈ�ɕ��@�I�ɐ��������ɂ��Ă͌��m���܂���B�Ⴆ�΁A�R�����g�J�n������͍s���ɂȂ��Ă͂Ȃ�Ȃ��Ƃ�������d�l���������Ƃ��Ă��A
 * �s�̓r���Ō��o�����΂���̓R�����g�J�n������Ƃ݂Ȃ��܂��B</li>
 * <li>�e�L�X�g�̏I���܂ŃR�����g�I��������A���邢�͕��̈��p����������Ȃ��ꍇ�́A���s���G���[�ƂȂ�܂��B</li>
 * </ul>
 *
 * <p>[��������]</p>
 * <ul>
 * <li>�q�A�h�L�������g�ɂ͑Ή����Ă��Ȃ����߁A�q�A�h�L�������g���ł��R�����g�����񂪌��o����܂��B</li>
 * <li>���ꖈ�̈��p���ȊO�ɂ�����ȃ��e����������ɂ͑Ή����Ă��Ȃ����߁A����烊�e���������񒆂ł��R�����g�����񂪌��o����܂��B</li>
 * <li>�R�����g�J�n������ɑ��������񂪉��ł��낤�ƃR�����g�Ƃ݂Ȃ����߁A�Ⴆ��PHP�̏ꍇ�A
 * �R�����g�s�̓r���ɃR�[�h�I���^�O?>������ꍇ�ł��s���܂ŃR�����g�Ƃ݂Ȃ���܂��B</li>
 * <li>��L�̕ʂ̐�����Ƃ��āA�Ⴆ��VB��rem���R�����g�J�n������Ƃ��Ē�`����ꍇ�A
 * "rem "�Ȃǂ̂悤�ɋ󔒕�����t�^����K�v������܂��i�����łȂ���remxxx�̂悤�ȕ�������R�����g�ł���Ƃ݂Ȃ���邽�߁j�B</li>
 * </ul>
 */
public class CommentRemover {

    /** �������[�h */
    public enum Mode {
        /** �R�����g�폜 */
        REMOVE,
        /** �R�����g���o */
        EXTRACT,
    }

    private CommentRemover() {
    }

    /**
     * �w�肳�ꂽ�e�L�X�g�f�[�^����A�w�肳�ꂽ�|���V�[�ɏ]���āA�R�����g���폜���܂��B
     * 
     * @param lines   �e�L�X�g�f�[�^
     * @param policy  �R�����g�폜�|���V�[
     * @return �R�����g�폜��̃e�L�X�g�f�[�^
     * @throws ParseException  ��̓G���[�����������ꍇ
     */
    public static List<String> remove(List<String> lines, CommentRemovePolicy policy)
            throws ParseException {
        return execute(lines, policy, Mode.REMOVE);
    }

    /**
     * �w�肳�ꂽ�e�L�X�g�f�[�^����A�w�肳�ꂽ�|���V�[�ɏ]���āA�R�����g�𒊏o���܂��B
     * �܂�A{@link #remove(List, CommentRemovePolicy)}�Ƃ͋t�ɁA�R�����g�ȊO�����ׂč폜���܂��B
     * 
     * @param lines   �e�L�X�g�f�[�^
     * @param policy  �R�����g�폜�|���V�[
     * @return �R�����g���o��̃e�L�X�g�f�[�^
     * @throws ParseException  ��̓G���[�����������ꍇ
     */
    public static List<String> extract(List<String> lines, CommentRemovePolicy policy)
            throws ParseException {
        return execute(lines, policy, Mode.EXTRACT);
    }

    /**
     * �w�肳�ꂽ�e�L�X�g�f�[�^����A�w�肳�ꂽ�|���V�[�ɏ]���āA�R�����g���폜�܂��͒��o���܂��B
     * 
     * @param lines   �e�L�X�g�f�[�^
     * @param policy  �R�����g�폜�|���V�[
     * @param mode    �������[�h
     * @return �R�����g�폜�܂��͒��o��̃e�L�X�g�f�[�^
     * @see Mode
     * @throws IllegalArgumentException  �|���V�[�̐ݒ肪�s���ȏꍇ
     * @throws ParseException  ��̓G���[�����������ꍇ
     */
    public static List<String> execute(List<String> lines, CommentRemovePolicy policy, Mode mode)
            throws ParseException {
        // �R�����g�폜�|���V�[���`�F�b�N
        checkPolicy(policy);
        // �R�����g�폜�|���V�[�𐳏퉻
        CommentRemovePolicy normalizedPolicy = normalizedPolicy(policy);

        List<CodeParser> allParsers = new ArrayList<CodeParser>();
        List<String> result = new ArrayList<String>();

        // ���
        CodeParser parser = new CodeParser(lines, new CodePosition(0, 0), normalizedPolicy);
        do {
            allParsers.add(parser);
            parser = parser.next();
        } while(parser != null);

        // ��͌��ʂ���R�����g�폜 or ���o
        for (int i = 0; i < lines.size(); i++) {
            int row = i;
            List<CodeParser> parsers = allParsers.stream()
                    .filter(e -> e.withinRow(row)).collect(Collectors.toList());
            StringBuilder sb = new StringBuilder();
            boolean removed = false;

            for (CodeParser cp : parsers) {
                SimpleEntry<String, Boolean> e = cp.getString(row, mode);
                sb.append(e.getKey());
                removed |= e.getValue();
            }

            // �ǉ��Ώۂ̃f�[�^�ł���Ό��ʂɊi�[
            String line = sb.toString();
            if (willAdd(line, removed, normalizedPolicy)) {
                result.add(line);
            }
        }

        return result;
    }

    private static void checkPolicy(CommentRemovePolicy policy) {
        String[] beginComments = policy.getMultiLineCommentBegin();
        String[] endComments = policy.getMultiLineCommentEnd();

        if (beginComments != null ^ endComments != null) {
            throw new IllegalArgumentException("�R�����g�J�n������ƃR�����g�I��������̕Е�������L���ɂ��邱�Ƃ͂ł��܂���B");

        } else if (beginComments != null && endComments != null) {
            if (beginComments.length != endComments.length) {
                throw new IllegalArgumentException("�R�����g�J�n������ƃR�����g�I��������̐����قȂ�܂��B");
            }
            for (int i = 0; i < beginComments.length; i++) {
                if (isValid(beginComments[i]) ^ isValid(endComments[i])) {
                    throw new IllegalArgumentException("�R�����g�J�n������ƃR�����g�I��������̕Е�������L���ɂ��邱�Ƃ͂ł��܂���B");
                }
            }
        }
    }

    private static boolean isValid(String str) {
        return !(str == null || str.equals(""));
    }

    private static CommentRemovePolicy normalizedPolicy(CommentRemovePolicy policy) {
        CommentRemovePolicy result = new CustomCommentRemovePolicy(policy);
        String[] comments = result.getCommentString();
        String[] beginComments = result.getMultiLineCommentBegin();
        String[] endComments = result.getMultiLineCommentEnd();
        boolean found;

        /*
         * �R�����g������A�R�����g�J�n�����񂪕�����`����Ă���ꍇ�A���Ԃ𐮂���B
         * �Ⴆ�΁A/*�A/**�̏ꍇ��/**����Ɍ��������悤�ɂ��邽�߁A/*�����O�Ɉړ��B
         */
        if (comments != null) {
            do {
                found = false;
                for (int i = 0; i < comments.length - 1; i++) {
                    for (int j = i + 1; j < comments.length; j++) {
                        if (comments[i].length() < comments[j].length() &&
                            comments[j].startsWith(comments[i])) {
                            swap(comments, i, j);
                            found = true;
                            break;
                        }
                    }
                }
            } while (found);
        }

        if (beginComments != null) {
            do {
                found = false;
                for (int i = 0; i < beginComments.length - 1; i++) {
                    for (int j = i + 1; j < beginComments.length; j++) {
                        if (beginComments[i].length() < beginComments[j].length() &&
                            beginComments[j].startsWith(beginComments[i])) {
                            swap(beginComments, i, j);
                            swap(endComments, i, j);
                            found = true;
                            break;
                        }
                    }
                }
            } while (found);
        }

        return result;
    }

    private static void swap(String[] arr, int idx1, int idx2) {
        String tmp = arr[idx1];
        arr[idx1] = arr[idx2];
        arr[idx2] = tmp;
    }

    private static boolean willAdd(String line, boolean removed, CommentRemovePolicy policy) {
        // �폜���s���Ă��Ȃ��ꍇ
        if (!removed) {
            return true;
        }

        // ��s���폜����ݒ�A����s�̏ꍇ
        if (policy.isEnabledRemoveEmptyLine() &&
                line.equals("")) {
            return false;
        }

        // �󔒍s���폜����ݒ�A���󔒍s�̏ꍇ
        if (policy.isEnabledRemoveBlankLine() &&
                policy.getBlankPattern() != null &&
                policy.getBlankPattern().matcher(line).matches()) {
            return false;
        }
        return true;
    }
}