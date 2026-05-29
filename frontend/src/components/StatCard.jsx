export default function StatCard({ title, value, subtitle, icon: Icon, color = 'brand' }) {
  const colors = {
    brand:   'text-brand-400 bg-brand-900/30',
    green:   'text-emerald-400 bg-emerald-900/30',
    red:     'text-red-400 bg-red-900/30',
    yellow:  'text-yellow-400 bg-yellow-900/30',
  }

  return (
    <div className="card flex items-start gap-4">
      {Icon && (
        <div className={`p-3 rounded-xl ${colors[color]}`}>
          <Icon size={20} className={colors[color].split(' ')[0]} />
        </div>
      )}
      <div className="min-w-0">
        <p className="text-sm text-slate-400 font-medium">{title}</p>
        <p className="text-2xl font-bold text-white mt-0.5">{value}</p>
        {subtitle && <p className="text-xs text-slate-500 mt-0.5">{subtitle}</p>}
      </div>
    </div>
  )
}
