package rustleund.fightingfantasy.framework.base.audio

import java.nio.file.Path

data class AudioFile(
    val path: Path,
    val interrupt: Boolean
)
