package com.alf.passwordmanagerv2

import com.alf.passwordmanagerv2.utils.findField
import com.alf.passwordmanagerv2.utils.getFields
import com.alf.passwordmanagerv2.utils.saveFields
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class FieldsUnitTest {

    private val name1 = "Salt"
    private val value1 = "1234567890ABCDEF"
    private val name2 = "Hash"
    private val value2 = "1234567890ABCDEF"
    private val name3 = "Date"
    private val value3 = "00/00/0000 00:00:00"

    private fun createFile(fileName: String): File {
        val tmpFolder = TemporaryFolder()
        tmpFolder.create()

        val pair1 = Pair(name1, value1)
        val pair2 = Pair(name2, value2)
        val pair3 = Pair(name3, value3)

        val fields = listOf(pair1, pair2, pair3)

        saveFields(fields, "${tmpFolder.root}/$fileName")

        return File("${tmpFolder.root}")
    }

    @Test
    fun saveFields_isCorrect() {
        val fileName = "test.txt"
        val tmpFolder = createFile("test.txt")

        assertEquals(
            "File does not exist or there are more than one", 1, tmpFolder.listFiles()!!.size
        )
        assertEquals("File name is not correct", fileName, tmpFolder.listFiles()!!.first().name)

        val lines = tmpFolder.listFiles()!!.first().readLines()

        assertEquals("Amount of lines is not correct", 3, lines.size)
        assertEquals("First line is not correct", "$name1: $value1", lines[0])
        assertEquals("Second line is not correct", "$name2: $value2", lines[1])
        assertEquals("Third line is not correct", "$name3: $value3", lines[2])
    }

    @Test
    fun getFields_isCorrect() {
        val fileName = "test.txt"
        val tmpFolder = createFile("test.txt")

        val fields = getFields("${tmpFolder.absolutePath}/$fileName")

        assertEquals("Amount of fields is not correct", 3, fields.size)

        assertEquals("First field name is not correct", name1, fields[0].first)
        assertEquals("First field value is not correct", value1, fields[0].second)

        assertEquals("Second field name is not correct", name2, fields[1].first)
        assertEquals("Second field value is not correct", value2, fields[1].second)

        assertEquals("Third field name is not correct", name3, fields[2].first)
        assertEquals("Third field value is not correct", value3, fields[2].second)
    }

    @Test
    fun findField_isCorrect() {
        val fileName = "test.txt"
        val tmpFolder = createFile("test.txt")

        val filePath = "${tmpFolder.absolutePath}/$fileName"

        val field1 = findField(name1, filePath)
        val field2 = findField(name2, filePath)
        val field3 = findField(name3, filePath)

        assertEquals("First field value is not correct", value1, field1)
        assertEquals("Second field value is not correct", value2, field2)
        assertEquals("Third field value is not correct", value3, field3)
    }

}