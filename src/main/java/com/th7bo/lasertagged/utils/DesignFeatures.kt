package com.th7bo.lasertagged.utils

import com.th7bo.lasertagged.LaserTagged
import fr.mrmicky.fastboard.FastBoard
import org.bukkit.Bukkit.getServer
import org.bukkit.Statistic
import org.bukkit.entity.Player
import java.text.SimpleDateFormat


class DesignFeatures {
    var monthFormat: SimpleDateFormat? = SimpleDateFormat("MM");
    var dayFormat: SimpleDateFormat? = SimpleDateFormat("dd");
    var yearFormat: SimpleDateFormat? = SimpleDateFormat("yyyy");


    init {
        getServer().scheduler.runTaskTimer(LaserTagged.instance, Runnable {
            for (board in LaserTagged.instance.boards.values) {
                updateBoard(board)
            }
        }, 0, 20)
    }

    private fun updateBoard(board: FastBoard) {
        val player = board.player
        if (!player.isOnline) {
            return
        }
        val playerData = player.playerData()
        val monthInt: Int? = monthFormat?.format(System.currentTimeMillis())?.toInt()
        val months = arrayOf("january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december")
        val month = months[monthInt!! - 1].substring(0, 3).lowercase().replaceFirstChar { char -> char.titlecase() }
        val year = yearFormat?.format(System.currentTimeMillis())
        val day = dayFormat?.format(System.currentTimeMillis())

        val accuracy = playerData?.accuracy
        val hits = playerData?.hits
        val shots = playerData?.shots
        var misses = shots?.minus(hits!!)
        if (misses != null) {
            if (misses < 0) {
                misses = 0
            }
        }
        // default shots to 1 to avoid division by 0
        val percent = accuracy?.times(100);


        board.updateLines(
            ("         &7$day $month $year").color(),
            ("&6&l" + player.name).color(),
            (" &e┣ &fKILLS: &#ecd761" + playerData?.kills).color(),
            (" &e┣ &fDEATHS: &#ecd761" + playerData?.deaths).color(),
            (" &e┣ &fHITS: &#ecd761" + playerData?.hits).color(),
            (" &e┗ &fMISSES: &#ecd761$misses").color(),
            ("").color(),
            ("&6&lProgress").color(),
            (" &e┣ &fW/L: &#ecd761" + playerData?.wins + "/" + playerData?.losses).color(),
            (" &e┣ &fAcc: " +
                    accuracy?.let { Misc.createProgressBar(it, 1.0, 16, "&6", "&8")
            } + "&r &f$percent%").color(),
            (" &e┗ &fGames: &#ecd761" + playerData?.gamesPlayed).color(),
            ("").color(),
            ("  &fVoteparty: &60&f/&650").color(),
            ("&7store.name.xyz").color(),

        )
        board.updateTitle("§6§lLaserTagged")
    }

}