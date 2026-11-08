---
name: Campus Velocity & Vitality
colors:
  surface: '#f7f9fb'
  surface-dim: '#d8dadc'
  surface-bright: '#f7f9fb'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f4f6'
  surface-container: '#eceef0'
  surface-container-high: '#e6e8ea'
  surface-container-highest: '#e0e3e5'
  on-surface: '#191c1e'
  on-surface-variant: '#514532'
  inverse-surface: '#2d3133'
  inverse-on-surface: '#eff1f3'
  outline: '#837560'
  outline-variant: '#d5c4ac'
  surface-tint: '#7d5800'
  primary: '#7d5800'
  on-primary: '#ffffff'
  primary-container: '#ffb703'
  on-primary-container: '#6b4b00'
  inverse-primary: '#ffba27'
  secondary: '#006c49'
  on-secondary: '#ffffff'
  secondary-container: '#6cf8bb'
  on-secondary-container: '#00714d'
  tertiary: '#565e74'
  on-tertiary: '#ffffff'
  tertiary-container: '#bcc3dd'
  on-tertiary-container: '#495066'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffdea9'
  primary-fixed-dim: '#ffba27'
  on-primary-fixed: '#271900'
  on-primary-fixed-variant: '#5e4100'
  secondary-fixed: '#6ffbbe'
  secondary-fixed-dim: '#4edea3'
  on-secondary-fixed: '#002113'
  on-secondary-fixed-variant: '#005236'
  tertiary-fixed: '#dae2fd'
  tertiary-fixed-dim: '#bec6e0'
  on-tertiary-fixed: '#131b2e'
  on-tertiary-fixed-variant: '#3f465c'
  background: '#f7f9fb'
  on-background: '#191c1e'
  surface-variant: '#e0e3e5'
typography:
  display-lg:
    fontFamily: Space Grotesk
    fontSize: 36px
    fontWeight: '700'
    lineHeight: 44px
    letterSpacing: -0.03em
  display-lg-mobile:
    fontFamily: Space Grotesk
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 34px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Space Grotesk
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Space Grotesk
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
    letterSpacing: -0.01em
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 24px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '700'
    lineHeight: 18px
    letterSpacing: 0.01em
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.02em
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 11px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.04em
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-mobile: 0.75rem
  margin: 1.5rem
  margin-mobile: 1rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

The design system establishes a high-tempo, energetic, and safe peer-to-peer campus mobility environment. Engineered primarily for handheld mobile use under bright direct sunlight as well as low-light night-ride conditions, it projects an ethos of accessibility, youthful peer camaraderie, and uncompromised safety.

The aesthetic fuses **Tactile Modernism** with **Functional Glassmorphism**:
- Ultra-tactile, punchy controls engineered for instantaneous one-handed thumb interaction while on foot or on the move.
- Elevated translucency, frosted micro-surfaces, and high-visibility status cues that communicate real-time verification and live GPS tracking effortlessly.
- High-contrast, sharp graphic definition ensuring zero visual strain on mobile outdoor screens.

## Colors

The palette balances energetic speed with safety, trust, and environmental campus stewardship.

- **Primary (`#FFB703`)**: Rapido-inspired solar gold driving action, instant ride pickups, fare notifications, and dynamic primary CTAs.
- **Secondary (`#10B981`)**: Eco-green dedicated to peer trust indicators, verified college status badges, eco-savings trackers, and safe completion states.
- **Tertiary (`#0F172A`)**: Deep slate foundation providing crisp contrast for text hierarchies, structural containers, bottom sheets, and dark-mode baselines.
- **Neutral (`#F8FAFC`)**: Clean, anti-glare canvas designed for daylight readability, paired with stark white (`#FFFFFF`) card surfaces.

### Semantic & Tracking Roles
- **Live State Active / In-Route**: `#0284C7` (Sky Blue) paired with glowing pulses of Primary Gold.
- **Emergency / SOS**: `#EF4444` (Signal Red) for instantaneous one-tap safety access.
- **Verified Student Seal**: Primary Secondary Emerald `#10B981` paired with a soft 15% opacity halo.

## Typography

Typography prioritizes swift scanning under moving conditions:
- **Headlines (`Space Grotesk`)**: Tech-forward, geometric proportions with distinctive character cuts. Delivers athletic momentum for ride ETAs, live speed indicators, and destination titles.
- **Body & Controls (`Plus Jakarta Sans`)**: Humanist, open counterforms, and gentle rounding ensure immediate legibility for student names, hostel pickup spots, split-fare meters, and route instructions.

## Layout & Spacing

A mobile-first fluid layout built on an 8pt structural rhythm, optimized for dynamic touch zones across common smartphone viewports (360px to 430px wide):

- **Bottom Sheet Architecture**: Critical real-time ride data anchors to bottom modular sliding panels, keeping touch targets strictly within the lower 40% thumb zone.
- **Safe Margins**: Mobile layout observes a `16px` (`margin-mobile`) horizontal canvas gutter, expanding to `24px` on tablet breakpoints.
- **Vertical Rhythm**: Element spacing defaults to `space-sm` (8px) for tightly grouped meta-tags (e.g., helmet verified, battery level, peer rating) and `space-md` (16px) for card body padding and input stacking.

## Elevation & Depth

Visual hierarchy combines clean translucent layering with tactile physical grounding to distinguish floating overlay sheets from live map backgrounds.

- **Level 1 (Map Floats & Search Anchors)**: `rgba(255, 255, 255, 0.85)` surface with `backdrop-filter: blur(16px)`, backed by a soft ambient drop shadow: `0 8px 24px -4px rgba(15, 23, 42, 0.08)`.
- **Level 2 (Active Ride Cards & Bottom Sheets)**: Solid `#FFFFFF` in light mode or `#1E293B` in dark mode, framed with a 1px frosted outline (`rgba(255, 255, 255, 0.2)` or `rgba(15, 23, 42, 0.06)`) and ambient shadow: `0 12px 32px -4px rgba(15, 23, 42, 0.14)`.
- **Level 3 (Emergency, Alerts & Dialogs)**: High elevation `0 20px 40px -8px rgba(15, 23, 42, 0.22)` with backdrop screen dimming of `rgba(15, 23, 42, 0.6)`.
- **Dark Mode Elevation**: Eliminates diffuse black drop shadows in favor of tonal surface stepping (`#0F172A` → `#1E293B` → `#334155`) augmented by luminous micro-borders.

## Shapes

The shape system adopts a pill-centric, aerodynamic language (`roundedness: 3`):
- **Buttons, Badges, and Chips**: Fully pill-shaped (`9999px` / `rounded-full`) to deliver friendly, finger-friendly hit zones that communicate frictionless momentum.
- **Cards & Modal Sheets**: Styled with `rounded-xl` (2rem / 32px) top corners for bottom sheets, creating soft containment that sits gracefully above full-screen maps.
- **Input Fields**: Full pill curves (`rounded-full`), preventing rigid edges from clashing with quick-tap search patterns.

## Components

### Buttons
- **Primary Ride Action**: Full-width pill (`height: 56px`), energetic gold background (`#FFB703`), deep slate typography (`#0F172A`, weight 700). Features a subtle tactile inset top-highlight and dynamic scale down (`scale: 0.98`) on press.
- **Secondary / Eco Action**: Pill button with `#10B981` background, white bold text, used for green-ride matching and peer tipping.
- **Floating Map Controls**: Circular pills (`48x48px`) floating above map layers with glassmorphic backing and centered micro-icons.

### Status Badges & Verified Student Seals
- **Verified Student Badge**: Emerald pill (`#10B981` at 10% fill, `#10B981` text, 1px solid stroke `#10B981`), leading with an institutional graduation-cap or shield micro-icon.
- **Live Ride Tracking Pill**: Pulsing dot (`#FFB703`) + "Rider 2 mins away" text set in Slate `#0F172A` inside a glassmorphic container.

### Cards & Bottom Sheets
- **Ride Matching Card**: High-contrast white card featuring driver avatar, bike model, verified badge, and split-fare pill. Separators use ultra-light dashed borders mimicking ticketing stubs.
- **Route Selection Card**: Compact card showing hostel-to-department shortcuts with estimated walk/bike times in highlighted badges.

### Input Fields & Search Bars
- **Campus Destination Input**: Pill-form bar with a prominent pickup pin icon, clean placeholder text ("Where to on campus?"), and direct-access voice/shortcut buttons nested on the trailing edge.

### Real-Time Ride Tracking HUD
- **Persistent Tracker Bar**: Fixed bottom-pinned element displaying bike icon progress along a dual-color track (`#FFB703` active, `#E2E8F0` remaining), live OTP display box with high-contrast mono-spaced digits, and an instant SOS pill.