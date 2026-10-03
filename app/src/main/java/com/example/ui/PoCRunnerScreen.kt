package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.data.SandboxProfileEntity
import com.example.data.VirtualAppEntity
import com.example.virtual.StubActivity
import android.content.Intent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoCRunnerScreen(
    profiles: List<SandboxProfileEntity>,
    virtualApps: List<VirtualAppEntity>,
    onRunPoC: (Long, String, String, String, (String) -> Unit) -> Unit
) {
    var selectedProfile by remember { mutableStateOf<SandboxProfileEntity?>(null) }
    var selectedApp by remember { mutableStateOf<VirtualAppEntity?>(null) }
    var executionResult by remember { mutableStateOf("上のボタンをワンタップして、サンドボックスの仮想化テストを即座に実行できます！") }
    var isRunning by remember { mutableStateOf(false) }

    // Auto-select first profile & app if available
    LaunchedEffect(profiles, virtualApps) {
        if (selectedProfile == null && profiles.isNotEmpty()) {
            selectedProfile = profiles.first()
        }
        if (selectedApp == null && virtualApps.isNotEmpty()) {
            selectedApp = virtualApps.first()
        }
    }

    var profileExpanded by remember { mutableStateOf(false) }
    var appExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "サンドボックスPoC ＆ 仮想実行エンジン",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "複雑な設定は不要です。ワンタップで動的クラスロードとストレージ隔離をテストできます。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }

        // ONE-TAP QUICK EXECUTION CARD
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "⚡ ワンタップ簡単テスト",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Button(
                        onClick = {
                            val profile = selectedProfile ?: profiles.firstOrNull()
                            val app = selectedApp ?: virtualApps.firstOrNull()
                            if (profile != null && app != null) {
                                isRunning = true
                                onRunPoC(
                                    profile.id,
                                    profile.name,
                                    app.appName,
                                    "動的 DexClassLoader 実行"
                                ) { result ->
                                    executionResult = result
                                    isRunning = false
                                }
                            } else {
                                executionResult = "プロファイルまたは仮想アプリが見つかりません。"
                            }
                        },
                        enabled = !isRunning,
                        modifier = Modifier.fillMaxWidth().testTag("one_tap_run_button")
                    ) {
                        Text(if (isRunning) "実行中..." else "🚀 動的クラスロードテストを即座に実行")
                    }

                    Button(
                        onClick = {
                            val profile = selectedProfile ?: profiles.firstOrNull()
                            if (profile != null) {
                                isRunning = true
                                onRunPoC(
                                    profile.id,
                                    profile.name,
                                    "ストレージ ＆ コンテキスト",
                                    "隔離ストレージ書き込みテスト"
                                ) { result ->
                                    executionResult = result
                                    isRunning = false
                                }
                            } else {
                                executionResult = "プロファイルが見つかりません。"
                            }
                        },
                        enabled = !isRunning,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("📁 ストレージ隔離テストを即座に実行")
                    }

                    val context = LocalContext.current
                    Button(
                        onClick = {
                            val intent = Intent(context, StubActivity::class.java).apply {
                                putExtra(StubActivity.EXTRA_APK_PATH, context.packageCodePath ?: context.applicationInfo.sourceDir)
                                putExtra(StubActivity.EXTRA_CLASS_NAME, selectedApp?.mainActivityClass ?: "com.example.virtual.SamplePluginActivity")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Text("🛡️ StubActivityでプラグイン画面を起動")
                    }
                }
            }
        }

        item {
            Divider()
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "詳細設定（カスタム選択）",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Profile Selector
        item {
            ExposedDropdownMenuBox(
                expanded = profileExpanded,
                onExpandedChange = { profileExpanded = !profileExpanded }
            ) {
                OutlinedTextField(
                    value = selectedProfile?.name ?: "サンドボックスプロファイルを選択",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("サンドボックスプロファイル") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = profileExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor().testTag("profile_dropdown")
                )
                ExposedDropdownMenu(
                    expanded = profileExpanded,
                    onDismissRequest = { profileExpanded = false }
                ) {
                    if (profiles.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("利用可能なプロファイルがありません") },
                            onClick = { profileExpanded = false }
                        )
                    } else {
                        profiles.forEach { profile ->
                            DropdownMenuItem(
                                text = { Text("${profile.name} (${profile.accountName})") },
                                onClick = {
                                    selectedProfile = profile
                                    profileExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Virtual App Selector
        item {
            ExposedDropdownMenuBox(
                expanded = appExpanded,
                onExpandedChange = { appExpanded = !appExpanded }
            ) {
                OutlinedTextField(
                    value = selectedApp?.appName ?: "仮想アプリ / プラグインを選択",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("仮想アプリ") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = appExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor().testTag("app_dropdown")
                )
                ExposedDropdownMenu(
                    expanded = appExpanded,
                    onDismissRequest = { appExpanded = false }
                ) {
                    if (virtualApps.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("インポートされた仮想アプリがありません") },
                            onClick = { appExpanded = false }
                        )
                    } else {
                        virtualApps.forEach { app ->
                            DropdownMenuItem(
                                text = { Text(app.appName) },
                                onClick = {
                                    selectedApp = app
                                    appExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Results Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "実行結果 / PoC出力ログ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = executionResult,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.testTag("execution_result_text")
                    )
                }
            }
        }
    }
}
