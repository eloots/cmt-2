package cmt.admin.command

import cmt.Helpers.extractExerciseNr

package object execution {

  def renumberExercise(exercise: String, exercisePrefix: String, newNumber: Int): String =
    val newNumberPrefix = f"${exercisePrefix}_$newNumber%03d_"
    val oldNumberPrefix =
      f"${exercisePrefix}_${extractExerciseNr(exercise)}%03d_"
    exercise.replaceFirst(oldNumberPrefix, newNumberPrefix)
}
