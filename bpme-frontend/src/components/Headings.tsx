import React from "react";

function Headings() {
  return <div>Headings</div>;
}

export function Heading1({
  lightVariant,
  children,
}: {
  lightVariant: boolean;
  children: React.ReactNode;
}) {
  const fontClass = lightVariant ? "font-medium" : "font-semibold";
  return (
    <h1 className={`text-black ${fontClass} text-h1 font-['Manrope']`}>
      {children}
    </h1>
  );
}

export function Heading2({
  lightVariant,
  children,
}: {
  lightVariant: boolean;
  children: React.ReactNode;
}) {
  const fontClass = lightVariant ? "font-medium" : "font-semibold";
  return (
    <h2 className={`text-black  text-h2 ${fontClass} font-['Manrope']`}>
      {children}
    </h2>
  );
}

export function Heading3({
  lightVariant,
  children,
}: {
  lightVariant: boolean;
  children: React.ReactNode;
}) {
  const fontClass = lightVariant ? "font-medium" : "font-semibold";

  return (
    <h3 className={`text-black text-h3 ${fontClass} font-['Manrope']`}>
      {children}
    </h3>
  );
}

export function Heading4({
  lightVariant,
  children,
}: {
  lightVariant: boolean;
  children: React.ReactNode;
}) {
  const fontClass = lightVariant ? "font-medium" : "font-semibold";

  return (
    <h4 className={`text-black text-h4 ${fontClass} font-['Manrope']`}>
      {children}
    </h4>
  );
}
export function Heading5({
  lightVariant,
  children,
}: {
  lightVariant: boolean;
  children: React.ReactNode;
}) {
  const fontClass = lightVariant ? "font-medium" : "font-semibold";

  return (
    <h5 className={`text-black text-h5 ${fontClass} font-['Manrope']`}>
      {children}
    </h5>
  );
}

export function Heading6({
  lightVariant,
  children,
}: {
  lightVariant: boolean;
  children: React.ReactNode;
}) {
  const fontClass = lightVariant ? "font-medium" : "font-semibold";

  return (
    <h6
      className={`text-black text-h6 ${fontClass} font-normal font-['Manrope']`}
    >
      {children}
    </h6>
  );
}
