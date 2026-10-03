package com.example.virtual

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.widget.Toast

/**
 * A sample plugin Activity implementation bundled in the app package
 * that can be loaded and executed dynamically via StubActivity without being
 * pre-registered in AndroidManifest.xml.
 */
class SamplePluginActivity : IPluginActivity {
    private var hostActivity: Activity? = null

    override fun attach(activity: Activity) {
        this.hostActivity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        hostActivity?.let { ctx ->
            val layout = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(48, 48, 48, 48)
                setBackgroundColor(Color.parseColor("#FAFAFA"))
            }

            val title = TextView(ctx).apply {
                text = "🛡️ 仮想化プラグイン画面 (Stub Activity経由)"
                textSize = 22f
                setTextColor(Color.parseColor("#1565C0"))
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val desc = TextView(ctx).apply {
                text = "このActivityはホストのAndroidManifest.xmlに事前登録されていません！\n\nDexClassLoader経由で動的にロードされ、ホストの「StubActivity」が身代わり（プロキシコンテナ）としてライフサイクルと画面描画を仲介しています。"
                textSize = 15f
                setPadding(0, 24, 0, 32)
                setTextColor(Color.parseColor("#424242"))
            }

            val actionButton = Button(ctx).apply {
                text = "プラグイン内アクション実行"
                setOnClickListener {
                    Toast.makeText(ctx, "プラグインActivityが正常に応答しました！", Toast.LENGTH_SHORT).show()
                }
            }

            val closeButton = Button(ctx).apply {
                text = "画面を閉じる"
                setOnClickListener {
                    ctx.finish()
                }
            }

            layout.addView(title)
            layout.addView(desc)
            layout.addView(actionButton)
            layout.addView(closeButton)

            ctx.setContentView(layout)
        }
    }

    override fun onStart() {}
    override fun onResume() {}
    override fun onPause() {}
    override fun onStop() {}
    override fun onDestroy() {}
}
