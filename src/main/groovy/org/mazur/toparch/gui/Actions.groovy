package org.mazur.toparch.gui

import java.text.DecimalFormat
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.data.category.CategoryDataset
import org.jfree.chart.ChartPanel
import org.mazur.toparch.analyze.TopologyInfo
import groovy.swing.SwingBuilder
import java.text.NumberFormat;

import javax.swing.JTable
import javax.swing.JTabbedPane
import org.jfree.chart.plot.PlotOrientation
import static org.jfree.chart.ChartFactory.*

/**
 * GUI actions.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
class Actions {

  private static SwingBuilder swing = new SwingBuilder()
  
  public static def zonesSource
  
  public static def content
  
  private static double tf(double value) { return Math.abs(1 - value) }
  
  private static def optimalSelectors = [
    s : { result, current ->
      return result == null ? current : result.s < current.s ? current : result
    },
    d : { result, current ->
      return result == null ? current : result.d > current.d ? current : result
    },
    ad : { result, current ->
      return result == null ? current : result.ad > current.ad ? current : result
    },
    c : { result, current ->
      return result == null ? current : result.c > current.c ? current : result
    },
    t : { result, current ->
      return result == null ? current : tf(result.t) > tf(current.t) ? current : result
    }
  ]
  
  private static def divideToZones() {
    def ns = zonesSource.text.split(/\s*;\s*/).collect { Integer.parseInt(it) }
    int rc = ns.size() + 1
    def result = new ArrayList(rc)
    rc.times() {
      result += new HashMap(content.size())
    }
    content.each() { key, infoList ->
      int i = 0
      for (TopologyInfo ti in infoList) {
        if (i < ns.size() && ti.n > ns[i]) { i++ }
        def currentResult = result[i]
        if (!currentResult[key]) { currentResult[key] = new LinkedList() }
        currentResult[key] += ti
      }
      
    }
    return result
  }
  
  private static def chartData(final def zonesData, final String param) {
    DefaultCategoryDataset result = new DefaultCategoryDataset()
    zonesData.eachWithIndex { infoMap, zIndex -> 
      String cat = "${zIndex + 1}"
      infoMap[0].each() { key, info ->
        result.addValue info[param], key, cat
      }
    }
    return result
  }
  private static def chartSumData(final def zonesData) {
    DefaultCategoryDataset result = new DefaultCategoryDataset()
    zonesData.eachWithIndex { infoMap, zIndex -> 
      String cat = "${zIndex + 1}"
      infoMap[0].each() { key, info ->
        double sum = 0
        TopologyInfo.valueFields().each() { sum += info[it] }
        result.addValue sum, key, cat
      }
    }
    return result
  }
  
  public static final def ANALYZE = swing.action(
    name : "Analyze",
    closure : {
      def zones = divideToZones() 
      def parameters = TopologyInfo.valueFields()
      println zones
      def zonesData = new LinkedList()
      zones.each() { infoMap ->
        def avgMap = new LinkedHashMap()
        infoMap.each() { key, infoList ->
          def avg = infoList.inject(new TopologyInfo()) { sum, cur -> sum + cur}
          if (key == 'pyramids') { println avg }
          avg /= infoList.size()
          if (key == 'pyramids') { println avg }
          avgMap[key] = avg
        }
        def normMap = new LinkedHashMap(avgMap)
        normMap.each() { key, value ->
          normMap[key] = value.clone()
        }
        parameters.each() { param ->
          // define optimal
          def o = normMap.values().inject(null) { result, current ->
            return optimalSelectors[param](result, current)
          }
          o = o.clone()
          if (o.t < 1) { o.t = 2 - o.t }
          // normalize
          normMap.each() { key, info ->
            switch (param) {
            case 's': info[param] = o[param] / info[param]; break
            case 't': if (info.t < 1) { info.t = 2 - info.t }
            default: info[param] /= o[param]
            }
          }
        }
        println avgMap
        zonesData.add([normMap, avgMap])
      }
      
      def pColumns = []
      parameters.each { pColumns += [it.toUpperCase(), it.toUpperCase() + " Avg"] }
      def columns = (["Zone", "Topology"] + pColumns + ["Sum"]).toArray()
      int rowsCount = zonesData[0][0].size() * zonesData.size() 
      def tableData = new Object[rowsCount][columns.length]
      int i = 0
      zonesData.eachWithIndex { infoMap, zIndex -> 
        tableData[i][0] = zIndex + 1
        int lastI = i
        infoMap[0].each() { key, info ->
          tableData[i][1] = key
          double sum = 0
          parameters.eachWithIndex { p, j ->
            NumberFormat nf = new DecimalFormat()
            tableData[i][2 + j * 2] = nf.format(info[p])
            sum += info[p]
          }
          tableData[i][columns.length - 1] = sum
          i++
        }
        i = lastI
        infoMap[1].each() { key, info ->
          parameters.eachWithIndex { p, j -> 
            NumberFormat nf = new DecimalFormat()
            tableData[i][3 + j * 2] = nf.format(info[p]) 
          }
          i++
        }
      }
      
      def table = new JTable(tableData, columns)
      (swing.frame(title : "Results", pack : true) {
        borderLayout()
        tabbedPane() {
          panel(name : "Normalized data") {
            borderLayout()
            scrollPane() { widget(table) }
          }
          panel(name : "Graphs") {
            borderLayout()
            tabbedPane(tabPlacement : JTabbedPane.LEFT) {
              for (String p in parameters) {
                panel(name : p.toUpperCase()) {
                  borderLayout()
                  def chart = createBarChart(p.toUpperCase(), "Zones", "",  chartData(zonesData, p), PlotOrientation.VERTICAL, true, true, false)
                  widget(new ChartPanel(chart))
                }
              }
              panel(name : "Sum") {
                borderLayout()
                def chart = createBarChart("Sum", "Zones", "",  chartSumData(zonesData), PlotOrientation.VERTICAL, true, true, false)
                widget(new ChartPanel(chart))
              }
            }
          }
        }
      }).visible = true
    }
  )
  
  private Actions() { }
  
}
