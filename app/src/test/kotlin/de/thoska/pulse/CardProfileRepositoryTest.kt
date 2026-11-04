package de.thoska.pulse

import de.thoska.pulse.data.CardProfileRepository
import de.thoska.pulse.model.CardProfile
import de.thoska.pulse.model.ThoskaCardData
import de.thoska.pulse.model.ThuringiaUniversity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CardProfileRepositoryTest {

    @Test
    fun `test normalize UID helper`() {
        assertEquals("043156CA6C2490", CardProfileRepository.normalizeUid("04:31:56:CA:6C:24:90"))
        assertEquals("043156CA6C2490", CardProfileRepository.normalizeUid("04:31:56:ca:6c:24:90"))
        assertEquals("043156CA6C2490", CardProfileRepository.normalizeUid("04 31 56 CA 6C 24 90"))
        assertEquals("043156CA6C2490", CardProfileRepository.normalizeUid("043156ca6c2490"))
    }

    @Test
    fun `test thoska card data profile binding and german comma formatting`() {
        val profile = CardProfile(
            cardUid = "04:31:56:CA:6C:24:90",
            holderName = "MAX MUSTERMANN",
            matrikelNr = "1234567",
            libraryBarcode = "987654321012",
            validUntil = "01.10.26 - 31.03.27",
            universityId = ThuringiaUniversity.FSU_JENA.id
        )

        val cardData = ThoskaCardData(
            cardUid = "04:31:56:CA:6C:24:90",
            balanceEur = 40.40,
            lastTransactionEur = 1.40,
            selectedAid = "5F8415",
            profile = profile
        )

        assertTrue(cardData.isRecognizedProfile)
        assertEquals("40,40 €", cardData.formattedBalance)
        assertEquals("1,40 €", cardData.formattedLastTransaction)
        assertEquals("MAX MUSTERMANN", cardData.profile?.holderName)
        assertEquals(ThuringiaUniversity.FSU_JENA, cardData.profile?.university)
        assertEquals("FSU JENA", cardData.profile?.university?.shortName)
    }

    @Test
    fun `test guest card data without profile`() {
        val guestCard = ThoskaCardData(
            cardUid = "04:99:AA:BB:CC:DD:EE",
            balanceEur = 12.50,
            lastTransactionEur = 2.80,
            selectedAid = "5F8415",
            profile = null
        )

        assertFalse(guestCard.isRecognizedProfile)
        assertNull(guestCard.profile)
        assertEquals("12,50 €", guestCard.formattedBalance)
        assertEquals("2,80 €", guestCard.formattedLastTransaction)
    }

    @Test
    fun `test default university fallback on CardProfile`() {
        val profile = CardProfile(
            cardUid = "04:11:22:33:44:55:66",
            holderName = "JANE DOE",
            matrikelNr = "7654321",
            libraryBarcode = "123456789012",
            validUntil = "31.03.27"
        )
        assertEquals(ThuringiaUniversity.EAH_JENA.id, profile.universityId)
        assertEquals(ThuringiaUniversity.EAH_JENA, profile.university)
    }
}
