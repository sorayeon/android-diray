package shop.sorayeon.android.diary

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener

class DiaryActivity: AppCompatActivity() {

    // 다이어리 수정 에디터
    private val diaryEditText: EditText by lazy {
        findViewById<EditText>(R.id.diaryEditText)
    }

    // Thread 와 Thread 간의 통신을 엮어주는 기능
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        // SharedPreferences 에 저장된 내용(content)을 가져와 다이어리 에디터에 세팅한다.
        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        diaryEditText.setText(sharedPreferences.getString("content", ""))

        // Runnable 다른 스레드에서 SharedPreferences 저장하는 기능
        val runnable = Runnable {
            sharedPreferences.edit {
                putString("content", diaryEditText.text.toString())
            }
        }

        // 다이어리 에디터 (diaryEditText) 텍스트 내용이 변경되는 이벤트
        diaryEditText.addTextChangedListener {
            // 0.5초 이내 더이상 입력이 없으면 백그라운드에서 내용을 SharedPreferences 저정 (계속 타이핑되는 상황에서는 저장되지 않음)
            // 핸들러 메세지 큐에 등록된 runnable 을 삭제
            handler.removeCallbacks(runnable)
            // 핸들러 : 메세지 큐에 0.5초 후 실행하는 runnable 전달
            handler.postDelayed(runnable, 500)
        }
    }
}