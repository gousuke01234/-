package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.VirtualAppEntity

@Composable
fun VirtualAppsScreen(
    virtualApps: List<VirtualAppEntity>,
    onAddVirtualApp: (String, String, Int, String, String) -> Unit,
    onDeleteVirtualApp: (VirtualAppEntity) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var appNameInput by remember { mutableStateOf("") }
    var pathInput by remember { mutableStateOf("/sdcard/Download/plugin_sample.apk") }
    var classInput by remember { mutableStateOf("com.sandbox.plugin.VirtualPluginActivity") }
    var sdkInput by remember { mutableStateOf("34") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier.testTag("add_virtual_app_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "仮想アプリ追加")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "仮想アプリ ＆ プラグイン リポジトリ",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "システムにインストールせず、DexClassLoader経由で動的にロードされる外部APKおよびDEXバイナリ。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            if (virtualApps.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("仮想アプリがまだインポートされていません。")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { showDialog = true }) {
                                Text("サンプルプラグインのインポート")
                            }
                        }
                    }
                }
            } else {
                items(virtualApps) { app ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Apps, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = app.appName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text(text = "ターゲットSDK: ${app.targetSdk} | バージョン: ${app.version}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                    }
                                }
                                IconButton(onClick = { onDeleteVirtualApp(app) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "削除", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "パス: ${app.apkPath}", style = MaterialTheme.typography.bodySmall)
                            Text(text = "メインクラス: ${app.mainActivityClass}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("仮想APK / プラグインのインポート") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = appNameInput,
                        onValueChange = { appNameInput = it },
                        label = { Text("アプリ名 (例: セキュアチャット)") },
                        modifier = Modifier.fillMaxWidth().testTag("app_name_input")
                    )
                    OutlinedTextField(
                        value = pathInput,
                        onValueChange = { pathInput = it },
                        label = { Text("APK / DEX パス") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = classInput,
                        onValueChange = { classInput = it },
                        label = { Text("メインアクティビティクラス") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = sdkInput,
                        onValueChange = { sdkInput = it },
                        label = { Text("ターゲットSDK") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (appNameInput.isNotBlank()) {
                            onAddVirtualApp(
                                appNameInput,
                                pathInput,
                                sdkInput.toIntOrNull() ?: 34,
                                classInput,
                                "1.0.0"
                            )
                            appNameInput = ""
                            showDialog = false
                        }
                    }
                ) {
                    Text("インポート")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("キャンセル")
                }
            }
        )
    }
}
