package gg.harvester;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsoleProgressBar implements Closeable {

  private final int total;
  private final AtomicInteger current = new AtomicInteger(0);
  private final long startTime;
  private final String taskName;

  private final int barWidth;
  private static Terminal terminal = null;

  public ConsoleProgressBar(String taskName, int total) {
    this.total = total;
    this.taskName = taskName;
    this.startTime = System.currentTimeMillis();

    // Try to use full terminal width, fallback to 80
    int width = 80;
    try {
      width = getTerminal().getWidth();
      closeTerminal();
    } catch (Exception ignored) {}

    this.barWidth = Math.max(20, width - (taskName.length() + 40)); // leave space for text


  }

  public void step() {
    stepBy(1);
  }

  public void stepBy(int n) {
    int done = current.addAndGet(n);
    print(done);
  }

  private void print(int done) {
    double progress = (double) done / total;
    int percent = (int) (progress * 100);

    int filled = (int) (progress * barWidth);

    StringBuilder bar = new StringBuilder();
    bar.append("[");

    for (int i = 0; i < barWidth; i++) {
      bar.append(i < filled ? "=" : " ");
    }

    bar.append("]");

    long now = System.currentTimeMillis();
    long elapsedMs = now - startTime;

    double rate = done / (elapsedMs / 1000.0 + 0.0001); // avoid div by zero
    long etaSeconds = (long) ((total - done) / (rate + 0.0001));

    String eta = formatDuration(etaSeconds);

    String line = String.format(
      "%s %s %3d%% (%d/%d)  | ETA %s",
      taskName,
      bar,
      percent,
      done,
      total,
      eta
    );

    System.out.println(line);
  }

  private String formatDuration(long seconds) {
    Duration d = Duration.ofSeconds(seconds);
    long h = d.toHours();
    long m = d.toMinutesPart();
    long s = d.toSecondsPart();

    if (h > 0) {
      return String.format("%dh %02dm %02ds", h, m, s);
    } else if (m > 0) {
      return String.format("%dm %02ds", m, s);
    } else {
      return String.format("%ds", s);
    }
  }

  synchronized static void closeTerminal() {
    try {
      if (terminal != null) {
        terminal.close();
        terminal = null;
      }
    } catch (IOException ignored) { /* noop */ }
  }

  static Terminal getTerminal() {
    if (terminal == null) {
      try {
        // Issue #42
        // Defaulting to a dumb terminal when a supported terminal can not be correctly created
        // see https://github.com/jline/jline3/issues/291
        terminal = TerminalBuilder.builder().dumb(true).build();
      } catch (IOException e) {
        throw new RuntimeException("This should never happen! Dumb terminal should have been created.");
      }
    }
    return terminal;
  }

  @Override
  public void close() throws IOException {
    closeTerminal();
  }
}
