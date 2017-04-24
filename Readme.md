Solarisエビデンス収集ツール
===========================

システム概要
------------

Solaris サーバのシステム構成情報を収集します。

注意事項
--------

ssh接続時にログインプロンプトが日本語名の"パスワード:"の表示の場合、ssh接続に失敗する
問題があります。その場合は、以下ロケールの設定変更が必要になります。
rootユーザで、/etc/default/initファイルを開き、以下の行を追加する。

```
LANG=C
```

ToDo
----

v1.11でSSHライブラリをjschからganymed-ssh2に変更。
従来のAPIと比べて、以下が未実装となり、注意が必要

* sudo コマンド
* SFTP コマンド


ビルド方法
----------

GitHub サイトからリポジトリをクローンして、以下のエクスポートコマンドを実行します。

```
cd server-acceptance-solaris
getconfig -x ../server-acceptance-solaris.zip
      [zip] Building zip: /work/gradle/server-acceptance-solaris.zip
```

利用方法
--------

1. プロジェクトディレクトリ下に、 server-acceptance-solaris.zip を解凍します。
2. 「solarisチェックシート.xlsx」を開き、シート「チェック対象」に検査対象サーバの接続情報を記入します。
3. config/config-solaris.groovy 内のサーバアカウント情報を編集します。
4. server-acceptance ディレクトリに移動し、以下の getconfig コマンドを実行します。

```
getconfig -c ./config/config-solaris.groovy
```

AUTHOR
-----------

Minoru Furusawa <minoru.furusawa@toshiba.co.jp>

COPYRIGHT
-----------

Copyright 2014-2017, Minoru Furusawa, Toshiba corporation.
