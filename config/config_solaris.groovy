// �����d�l�V�[�g��`

evidence.source = './Solaris�`�F�b�N�V�[�g.xlsx'
evidence.sheet_name_server = '�`�F�b�N�Ώ�'
evidence.sheet_name_rule = '�������[��'
evidence.sheet_name_spec = [
    'Solaris': '�Q�X�gOS�`�F�b�N�V�[�g(Solaris)'
]

// �������ʃt�@�C���o�͐�

evidence.target='./build/Solaris�`�F�b�N�V�[�g_<date>.xlsx'

// �������ʃ��O�f�B���N�g��

evidence.staging_dir='./build/log'

// CSV�ϊ��}�b�v

evidence.csv_item_map = [
    '�T�[�o��' :            'server_name',
    'IP�A�h���X' :          'ip',
    'Platform' :            'platform',
    'OS�A�J�E���gID' :      'os_account_id',
    'vCenter�A�J�E���gID' : 'remote_account_id',
    'VM�G�C���A�X��' :      'remote_alias',
    '����ID' :              'verify_id',
    '��r�ΏۃT�[�o��' :    'compare_server',
    'CPU��' :               'NumCpu',
    '��������' :            'MemoryGB',
    'ESXi��' :              'ESXiHost',
    'HDD' :                 'HDDtype',
]

// ���񉻂��Ȃ��^�X�N
// ����x���w������Ă��A�w�肵���h���C���^�X�N�̓V���A���Ɏ��s����

// test.serialization.tasks = ['vCenter']

// DryRun���[�h���O�ۑ���

test.dry_run_staging_dir = './src/test/resources/log/'

// �R�}���h�̎�̃^�C���A�E�g
// Windows,vCenter�̏ꍇ�A�S�R�}���h���܂Ƃ߂��o�b�`�X�N���v�g�̃^�C���A�E�g�l

test.Solaris.timeout = 300

// �R�}���h�̎�̃f�o�b�O���[�h

// test.Solaris.debug  = false

// Solaris �ڑ����

account.Solaris.Test.user      = 'someuser'
account.Solaris.Test.password  = 'P@ssword'
account.Solaris.Test.work_dir  = '/tmp/gradle_test'
// account.Solaris.Test.logon_test = [['user':'test1' , 'password':'test1'],
//                                   ['user':'root'  , 'password':'P@ssw0rd']]

 