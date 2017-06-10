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

account.Solaris.Test.user      = 'root'
account.Solaris.Test.password  = 'Passw0rd'
account.Solaris.Test.work_dir  = '/tmp/gradle_test'
//account.Solaris.Test.logon_test = [['user':'psadmin', 'password':'minor17A'],
//                                   ['user':'root'  , 'password':'P@ssw0rd']]

 