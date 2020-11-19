package fr.enssat.babelblock.mentlia.taskblocks

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import timber.log.Timber
import java.util.*

private val LOCALES = arrayOf(
    Locale.FRENCH, Locale.ENGLISH, Locale.GERMAN, Locale.ITALIAN,
    Locale.JAPANESE, Locale.KOREAN, Locale.CHINESE,
)


class TaskBlockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = VERTICAL
    }

    private var taskBlock: TaskBlock? = null

    fun getTaskBlock(): TaskBlock? {
        return this.taskBlock
    }

    fun setTaskBlock(taskBlock: TaskBlock?) {
        this.taskBlock = taskBlock
        refreshView()
    }

    private fun refreshView() {
        removeAllViews()
        if (taskBlock == null) return

        val taskName = TextView(context)
        with(taskName) {
            layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            text = context.getText(taskBlock!!.getManifest().nameTextResource)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F)
            typeface = Typeface.DEFAULT_BOLD
        }
        addView(taskName)

        taskBlock!!.getManifest().additionalParameters.forEach {
            if (it.type == TaskBlockAdditionalParameterType.LANGUAGE) {
                val spinnerTitle = TextView(context)
                with(spinnerTitle) {
                    layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                    text = context.getText(it.nameResource)
                }
                addView(spinnerTitle)

                val spinner = Spinner(context)
                val spinnerAdapter =
                    ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item)
                var defaultValuePosition = -1
                for (i in LOCALES.indices) {
                    val locale = LOCALES[i]
                    spinnerAdapter.add(locale.displayLanguage.toString())
                    if (locale.toString().split("-")[0] == it.defaultValue) {
                        defaultValuePosition = i
                    }
                }

                with(spinner) {
                    layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                    adapter = spinnerAdapter
                    onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                pos: Int,
                                id: Long
                            ) {
                                taskBlock!!.setAdditionalParameter(it.id, LOCALES[pos].toString())
                            }
                        }
                }

                if (defaultValuePosition != -1) {
                    spinner.setSelection(defaultValuePosition)
                }

                addView(spinner)
            }
        }
    }
}