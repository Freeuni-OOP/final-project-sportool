const tickerItems = [
  'Join Tennis',
  'Book Court Now',
  'Find Your Rival',
  'Train After Dark',
  'Community',
  'Elite Matchdays',
];

export default function Ticker() {
  const repeatedItems = [...tickerItems, ...tickerItems];

  return (
    <section className="ticker" aria-label="SporTool highlights">
      <div className="ticker__track">
        {repeatedItems.map((item, index) => (
          <span className="ticker__item" key={`${item}-${index}`}>
            {item}
          </span>
        ))}
      </div>
    </section>
  );
}