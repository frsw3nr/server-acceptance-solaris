Solarisエビデンス収集ツール
===========================

システム概要
------------

Solaris サーバのシステム構成情報を収集します。

注意事項
--------

v1.11でSSHライブラリをjschからganymed-ssh2に変更。
従来のAPIと比べて、実装が異なります。

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
