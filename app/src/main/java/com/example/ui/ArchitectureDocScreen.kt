package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ArchitectureDocScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(Icons.Default.Architecture, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "サンドボックス アーキテクチャ設計",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Androidにおけるマルチアカウント仮想化と環境隔離の技術設計書。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }

        item {
            DocSection(
                title = "1. アプリの仮想化 (DexClassLoader)",
                content = "ホストの AndroidManifest.xml にアクティビティを事前登録することなく、`DexClassLoader` を使用して外部の APK または DEX ファイルを動的にロードします。スタブ/プロキシアクティビティ機構とリフレクションを活用してライフサイクルとメソッド呼び出しを仲介します。"
            )
        }

        item {
            DocSection(
                title = "2. コンテキストの分離 (ContextWrapper)",
                content = "各仮想プロファイルは `ContextWrapper` を継承した `IsolatedContextManager` をインスタンス化します。これにより `getApplicationContext()` やシステムサービス要求をインターセプトし、サンドボックスアプリがホストや他のプロファイルのデータにアクセスすることを防ぎます。"
            )
        }

        item {
            DocSection(
                title = "3. データストレージの隔離",
                content = "サンドボックスアプリの内部ストレージ（`files`、`cache`、`shared_prefs`）は、プロファイルごとの専用サブディレクトリツリー（`sandbox_profiles/profile_{id}/`）にルーティングされます。ファイルシステム操作が完全に隔離され、データ混入を防止します。"
            )
        }

        item {
            DocSection(
                title = "4. 互換性レイヤー (プロキシ機構)",
                content = "サンドボックスアプリがシステムサービス（位置情報、通知、アカウント管理など）を呼び出した際、互換性プロキシレイヤーがIPC呼び出しをインターセプトし、仮想化されたプロファイル固有のパラメータやモック（位置情報やトークンなど）を安全に返却します。"
            )
        }
    }
}

@Composable
fun DocSection(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
