package org.mazur.toparch.gui

import javax.swing.WindowConstants as WC
import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import javax.swing.JTabbedPane as JTB
import javax.swing.JTable
import org.mazur.toparch.analyze.TopologyInfo
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.ChartPanel
import org.jfree.data.xy.XYSeriesCollection
import org.jfree.data.xy.XYSeries
import static org.jfree.chart.ChartFactory.*
import static org.mazur.toparch.gui.Actions.*
import static org.mazur.toparch.gui.InfoReader.*

//def sources = [
//  circles   : "out-class org.mazur.toparch.CirclesGenerator.txt",
//  fibonachi : "out-class org.mazur.toparch.FibonachiGenerator.txt",
//  mesh      : "out-class org.mazur.toparch.MeshGenerator.txt",
//  pyramids  : "out-class org.mazur.toparch.PyramidsGenerator.txt",
//  spiral    : "out-class org.mazur.toparch.SpiralGenerator.txt"
//]

def sources = [
  hq3WithClusters : "out-class org.mazur.toparch.analyze.HQGenerator.txt",
  hq2 : "out-class org.mazur.toparch.analyze.HQClassicGenerator.txt",
]

content = [:]
sources.each() { key, fileName ->
  String info = new File(fileName).text
  content[key] = readInfo(info)
}

/** Source columns. */
def columns = TopologyInfo.fields().collect() { it.toUpperCase() }.toArray()

/**
 * @return data array for source tables
 */
def getTableData = { infoList ->
  def result = new Object[infoList.size()][columns.length]
  infoList.eachWithIndex { info, i -> 
    info.formRow().eachWithIndex { v, j -> result[i][j] = v }
  }
  return result
}

def chartData = { column ->
  String c = column.toLowerCase()
  XYSeriesCollection result = new XYSeriesCollection()
  content.each() { key, infoList -> 
    XYSeries series = new XYSeries(key)
    infoList.each() {
      series.add it.n, it[c]
    }
    result.addSeries series
  }
  return result
}

SwingBuilder.build {
  (frame(pack : true, defaultCloseOperation : WC.EXIT_ON_CLOSE, title : "toparch GUI") {
    tabbedPane() {
      // Sources
      panel(name : "Sources") {
        borderLayout()
        tabbedPane(constraints : BL.CENTER, tabPlacement : JTB.LEFT) {
          for (String tName in sources.keySet()) {
            panel(name : tName) {
              borderLayout()
              def t = new JTable(getTableData(content[tName]), columns)
              scrollPane() { widget(t) }
            }
          }
        }
      }
      
      // Diagrams
      panel(name : "Diagrams") {
        borderLayout()
        panel(constraints : BL.NORTH) {
          label(text : "Choose zones (intervals of N):")
          zonesSource = textField(text : "10;35")
          button(action : ANALYZE)
        }
        tabbedPane(constraints : BL.CENTER, tabPlacement : JTB.LEFT) {
          for (String tName in columns) {
            if (tName == "N") { continue }
            def chart = createXYLineChart(tName, "N", "", chartData(tName), PlotOrientation.VERTICAL, true, true, false);
            panel(name : tName) {
              borderLayout()
              widget(new ChartPanel(chart))
            }
          }
        }
      }
    }
  }).visible = true
}
