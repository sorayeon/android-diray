package shop.sorayeon.android.diary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    // 자물쇠번호 넘버피커1
    private val numberPicker1: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker1)
            .apply {
                // 0 ~ 9 까지 선택 가능하도록 초기화
                minValue = 0
                maxValue = 9
            }
    }
    private val numberPicker2: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker2)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }
    private val numberPicker3: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker3)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }

    // 다이어리 잠금해제 버튼
    private val openButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.openButton)
    }

    // 비밀번호 변경 버튼
    private val changePasswordButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.changePasswordButton)
    }

    // 비밀번호 변경 중 상태 (변경중엔 잠금해제 불가)
    private var changePasswordMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 액티비티가 생성될때 0~9 까지 선택 가능해야함 (선언만하면 lazy apply 안의 구문 수행)
        numberPicker1
        numberPicker2
        numberPicker3

        // 다이어리 잠금해제 버튼 클릭이벤트
        openButton.setOnClickListener {

            // 비밀번호 변경 모드인경우 예외처리
            if (changePasswordMode) {
                Toast.makeText(this, "비밀번호 변경 중입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 키-값 데이터를 저장할수 있는 공유환경설정 파일
            val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) // Context.MODE_PRIVATE 다른 앱들과 공유하지 않음

            // 사용자가 입력한 비밀번호
            val passwordFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

            // SharedPreferences 에 저장된 password 키를 가져옴. 값이 없다면 초기값은 000
            if (sharedPreferences.getString("password", "000").equals(passwordFromUser)) {
                // 패스워드 성공 -> 다이어리 페이지로 이동
                startActivity(Intent(this, DiaryActivity::class.java))
            } else {
                // 비밀번호 불일치 알럿
                showErrorAlertDialog()
            }
        }

        // 비밀번호 변경 버튼 클릭이벤트
        changePasswordButton.setOnClickListener {

            // 키-값 데이터를 저장할수 있는 공유환경설정 파일
            val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) // Context.MODE_PRIVATE 다른 앱들과 공유하지 않음
            val passwordFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

            // 번호 변경모드일때 변경버튼들 누르면 세팅된 비밀번호 저장
            if (changePasswordMode) {

//                val editor = passwordPreferences.edit()
//                editor.putString("password", "000")
//                editor.commit()

                // 입력한 번호를 저장하는 기능 (commit UI Thread 를 블락하고 데이터가 저장될때까지 기다림, apply 기다리지 않고 비동기방식으로 저장)
                sharedPreferences.edit(true) {
                    putString("password", passwordFromUser)
                }
                changePasswordMode = false
                changePasswordButton.setBackgroundColor(Color.BLACK)

            } else {
                // changePassWordMode 가 활성화 :: 비밀번호가 맞는지 체크

                // SharedPreferences 에 저장된 password 키를 가져옴. 값이 없다면 초기값은 000
                if (sharedPreferences.getString("password", "000").equals(passwordFromUser)) {

                    changePasswordMode = true
                    Toast.makeText(this, "변경할 패스워드를 입력해주세요", Toast.LENGTH_SHORT).show()
                    changePasswordButton.setBackgroundColor(Color.RED)

                } else {
                    // 비밀번호 불일치 알럿
                    showErrorAlertDialog()
                }
            }

        }
    }

    private fun showErrorAlertDialog() {
        // 단순 알럿 생성
        AlertDialog.Builder(this)
            .setTitle("실패")
            .setMessage("비밀번호가 잘못되었습니다.")
            .setPositiveButton("확인") { _, _ -> }
            .create()
            .show()
    }
}