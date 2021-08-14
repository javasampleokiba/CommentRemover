package javasampleokiba.commentremover.policy;

import java.util.regex.Pattern;

/**
 * �R�����g�폜�|���V�[�C���^�[�t�F�[�X.
 */
public interface CommentRemovePolicy {

    /**
     * �P��s�R�����g�̊J�n��������擾���܂��i�����w��j�B
     * {@code null}�܂��͋�z���Ԃ��ƁA�P��s�R�����g�폜�������ɂȂ�܂��B
     * 
     * @return �R�����g�J�n������
     */
    String[] getCommentString();

    /**
     * �����s�R�����g�̊J�n��������擾���܂��i�����w��j�B
     * {@code null}�܂��͋�z���Ԃ��ƁA�����s�R�����g�폜�������ɂȂ�܂��B
     * 
     * @return �����s�R�����g�J�n������
     */
    String[] getMultiLineCommentBegin();

    /**
     * �����s�R�����g�̏I����������擾���܂��i�����w��j�B
     * {@code null}�܂��͋�z���Ԃ��ƁA�����s�R�����g�폜�������ɂȂ�܂��B
     * �������w�肷��ꍇ�́A{@link #getMultiLineCommentBegin()}�Ə��Ԃ����킹�Ă��������B
     * 
     * @return �����s�R�����g�I��������
     */
    String[] getMultiLineCommentEnd();

    /**
     * �R�����g�폜�ɂ���ċ�s�ƂȂ����s���폜���邩���肵�܂��B
     * 
     * @return ��s���폜����ꍇ�� {@code true}
     */
    boolean isEnabledRemoveEmptyLine();

    /**
     * �R�����g�폜�ɂ���ċ󔒍s�ƂȂ����s���폜���邩���肵�܂��B
     * {@link #getBlankPattern()}�ŕԂ��ꂽ��������󔒕����Ƃ݂Ȃ��܂��B
     * 
     * @return �󔒍s���폜����ꍇ�� {@code true}
     */
    boolean isEnabledRemoveBlankLine();

    /**
     * �󔒕����Ƃ݂Ȃ�������i���K�\���j���擾���܂��B
     * {@code null}��Ԃ��Ƌ󔒕����w��Ȃ��ɂȂ�܂��B
     * 
     * @return �󔒕����i���K�\���j
     */
    Pattern getBlankPattern();

    /**
     * ���p����������擾���܂��i�����w��j�B
     * {@code null}��Ԃ��ƈ��p��������w��Ȃ��ɂȂ�܂��B
     * 
     * @return ���p��������
     */
    String[] getQuotationMarks();

    /**
     * �R�����g�폜���̍폜�Ώۂ̃R�����g�i���K�\���j���擾���܂��B
     * {@code null}��Ԃ��Ǝw��Ȃ��ɂȂ�A���ׂẴR�����g���폜�ΏۂɂȂ�܂��B
     * 
     * @return �폜�Ώۂ̃R�����g�i���K�\���j
     */
    Pattern getRemovePattern();

    /**
     * �R�����g�폜���̍폜�ΏۊO�̃R�����g�i���K�\���j���擾���܂��B
     * {@code null}��Ԃ��Ǝw��Ȃ��ɂȂ�A���ׂẴR�����g���폜�ΏۂɂȂ�܂��B
     * {@link #getRemovePattern()}���w�肳��Ă���ꍇ�͂����炪�D�悳��܂��B
     * 
     * @return �폜�ΏۊO�̃R�����g�i���K�\���j
     */
    Pattern getExceptPattern();
}