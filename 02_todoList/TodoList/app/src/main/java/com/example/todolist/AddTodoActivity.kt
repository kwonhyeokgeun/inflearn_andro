package com.example.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.todolist.databinding.ActivityAddTodoBinding
import com.example.todolist.db.AppDatabase
import com.example.todolist.db.TodoDao
import com.example.todolist.db.TodoEntity

class AddTodoActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddTodoBinding
    private lateinit var db : AppDatabase
    private lateinit var todoDao : TodoDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)!!
        todoDao = db.getTodo()

        binding.btnAdd.setOnClickListener {
            insertTodo()
        }

    }

    private fun insertTodo(){
        val title : String = binding.edtTitle.text.toString()
        val radioId : Int = binding.radioGroup.checkedRadioButtonId
        var importance : Int = 0

        when(radioId){
            R.id.rbtn_high -> {
                importance = 1
            }
            R.id.rbtn_mid -> {
                importance = 2
            }
            R.id.rbtn_low -> {
                importance = 3
            }
        }

        if(radioId==-1 || title.isBlank()){
            Toast.makeText(this, "제대로 입력해 주세요.",Toast.LENGTH_SHORT).show()
            return
        }

        Thread{
            todoDao.insert(TodoEntity(null, title, importance))

            runOnUiThread{ //ui는 UI스레드로
                Toast.makeText(this, "추가 되었습니다.",Toast.LENGTH_SHORT).show()
                finish() //이전화면으로 돌아가기
            }
        }.start()

    }
}