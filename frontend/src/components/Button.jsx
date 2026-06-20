export default function Button({
  children,
  className = '',
  variant = 'primary',
  href,
  ...props
}) {
  const classNames = `btn btn--${variant} ${className}`.trim();

  if (href) {
    return (
      <a className={classNames} href={href} {...props}>
        {children}
      </a>
    );
  }

  return (
    <button className={classNames} type="button" {...props}>
      {children}
    </button>
  );
}