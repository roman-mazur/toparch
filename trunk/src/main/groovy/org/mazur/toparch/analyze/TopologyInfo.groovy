package org.mazur.toparch.analyze

import java.text.DecimalFormat/**
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class TopologyInfo {

  /** Vertixes count. */
  double n = 0
  /** Max degree. */
  double s = 0
  /** Diameter. */
  double d = 0
  /** Average diameter. */
  double ad = 0
  /** Traffic. */
  double t = 0
  /** Cost. */
  double c = 0
  
  TopologyInfo plus(final TopologyInfo info) {
    this.s += info.s
    this.d += info.d
    this.ad += info.ad
    this.t += info.t
    this.c += info.c
    return this
  }
  
  TopologyInfo div(final Number n) {
    this.s /= n
    this.d /= n
    this.ad /= n
    this.t /= n
    this.c /= n
    return this
  }
  
  TopologyInfo clone() {
    return new TopologyInfo(n : n, s : s, t : t, d : d, ad : ad, c : c)
  }
  
  static def fields() {
    return TopologyInfo.class.declaredFields.name.grep(~/\w\w?/)
  }
  static def valueFields() { return fields().grep(~/[^n]+/) }
  
  def formRow() {
    fields().collect() { String.valueOf(this."$it") }
  }
  
  String serialize() {
    DecimalFormat f = new DecimalFormat()
    f.maximumFractionDigits = 2
    def result = new StringBuilder()
    fields().each() {
      def value = this."$it"
      result << f.format(value) << '\t'
    }
    result << '\n'
  }
  
  String toString() { return fields().inject("TopologyInfo: ") { res, f -> "$res ${f}=${this[f]}" } }
  
}
