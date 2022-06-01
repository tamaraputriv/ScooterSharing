package dk.itu.moapd.scootersharing
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test

class TextValidatorTest {

    @Test
    fun textValidator_CorrectNameSimple_ReturnsTrue() {
        assertThat(TextValidator.isValidName("Name"), `is`(true))
    }

    @Test
    fun textValidator_CorrectFullName_ReturnsTrue() {
        assertThat(TextValidator.isValidName("Name Surname"), `is`(true))
    }

    @Test
    fun textValidator_CorrectShortName_ReturnsTrue() {
        assertThat(TextValidator.isValidName("Ram"), `is`(true))
    }

    @Test
    fun textValidator_InvalidNameInitialSpace_ReturnsFalse() {
        assertThat(TextValidator.isValidName(" Name"), `is`(false))
    }

    @Test
    fun textValidator_InvalidNameFinalSpace_ReturnsFalse() {
        assertThat(TextValidator.isValidName("Name "), `is`(false))
    }

    @Test
    fun textValidator_InvalidNameLowerCase_ReturnsFalse() {
        assertThat(TextValidator.isValidName("name"), `is`(false))
    }

    @Test
    fun textValidator_InvalidShortName_ReturnsFalse() {
        assertThat(TextValidator.isValidName("Jo"), `is`(false))
    }

    @Test
    fun textValidator_EmptyNameString_ReturnsFalse() {
        assertThat(TextValidator.isValidName(""), `is`(false))
    }

    @Test
    fun textValidator_CorrectEmailSimple_ReturnsTrue() {
        assertThat(TextValidator.isValidEmail("name@itu.dk"), `is`(true))
    }

    @Test
    fun textValidator_CorrectEmailSubDomain_ReturnsTrue() {
        assertThat(TextValidator.isValidEmail("name@email.co.uk"), `is`(true))
    }

    @Test
    fun textValidator_InvalidEmailNoTld_ReturnsFalse() {
        assertThat(TextValidator.isValidEmail("name@itu"), `is`(false))
    }

    @Test
    fun textValidator_InvalidEmailDoubleDot_ReturnsFalse() {
        assertThat(TextValidator.isValidEmail("name@itu..dk"), `is`(false))
    }

    @Test
    fun textValidator_InvalidEmailNoUsername_ReturnsFalse() {
        assertThat(TextValidator.isValidEmail("@itu.dk"), `is`(false))
    }

    @Test
    fun textValidator_InvalidEmailNoDomain_ReturnsFalse() {
        assertThat(TextValidator.isValidEmail("name@"), `is`(false))
    }

    @Test
    fun textValidator_EmptyEmailString_ReturnsFalse() {
        assertThat(TextValidator.isValidEmail(""), `is`(false))
    }
}