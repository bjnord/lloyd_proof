# Author::     Brent J. Nordquist <mailto:brent@nordist.net>
# Copyright::  Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
# License::    {CC Attribution-ShareAlike 3.0 Unported}[http://creativecommons.org/licenses/by-sa/3.0/]

module ReturnStatus
  #--
  # <http://stackoverflow.com/questions/3357712/static-methods-in-ruby-modules>
  def self.included(o)
    o.extend(ClassMethods)
  end

  def save_and_return_status(sync_id)
    return {:sync_id => sync_id, :status => :ok} if self.save
    return {:sync_id => sync_id, :status => :error, :errors => self.errors.full_messages}
  end

  module ClassMethods
    def create_and_return_status(attr)
      sync_id = attr.delete(:sync_id)
      begin
        corr = Correction.create(attr)
        return {:sync_id => sync_id, :status => :ok} if corr.persisted?
        return {:sync_id => sync_id, :status => :error, :errors => corr.errors.full_messages}
      rescue ActiveModel::MassAssignmentSecurity::Error => e
        return {:sync_id => sync_id, :status => :error, :errors => [e.message]}
      end
    end
  end
end
