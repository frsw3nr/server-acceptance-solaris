// 検査仕様シート定義

evidence.source = './Solarisチェックシート.xlsx'
evidence.sheet_name_server = 'チェック対象'
evidence.sheet_name_rule = '検査ルール'
evidence.sheet_name_spec = [
    'Solaris': 'ゲストOSチェックシート(Solaris)'
]

// 検査結果ファイル出力先

evidence.target='./build/Solarisチェックシート_<date>.xlsx'

// 検査結果ログディレクトリ

evidence.staging_dir='./build/log'

// CSV変換マップ

evidence.csv_item_map = [
    'サーバ名' :            'server_name',
    'IPアドレス' :          'ip',
    'Platform' :            'platform',
    'OSアカウントID' :      'os_account_id',
    'vCenterアカウントID' : 'remote_account_id',
    'VMエイリアス名' :      'remote_alias',
    '検査ID' :              'verify_id',
    '比較対象サーバ名' :    'compare_server',
    'CPU数' :               'NumCpu',
    'メモリ量' :            'MemoryGB',
    'ESXi名' :              'ESXiHost',
    'HDD' :                 'HDDtype',
]

// 並列化しないタスク
// 並列度を指定をしても、指定したドメインタスクはシリアルに実行する

// test.serialization.tasks = ['vCenter']

// DryRunモードログ保存先

test.dry_run_staging_dir = './src/test/resources/log/'

// コマンド採取のタイムアウト
// Windows,vCenterの場合、全コマンドをまとめたバッチスクリプトのタイムアウト値

test.Solaris.timeout = 300

// コマンド採取のデバッグモード

// test.Solaris.debug  = false

// Solaris 接続情報

account.Solaris.Test.user      = 'someuser'
account.Solaris.Test.password  = 'P@ssword'
account.Solaris.Test.work_dir  = '/tmp/gradle_test'
// account.Solaris.Test.logon_test = [['user':'test1' , 'password':'test1'],
//                                   ['user':'root'  , 'password':'P@ssw0rd']]

 